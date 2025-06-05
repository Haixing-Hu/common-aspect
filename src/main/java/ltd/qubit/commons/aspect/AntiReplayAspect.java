////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import ltd.qubit.commons.annotation.AntiReplay;
import ltd.qubit.commons.error.OperationTooFrequentException;
import ltd.qubit.commons.util.codec.Base64Utils;

/**
 * 防重放攻击切面。
 * <p>
 * 该切面通过在方法执行前检查 Redis 中是否存在特定的锁，来防止方法的重复执行。
 * 主要用于处理客户端的重复请求，确保在指定时间内同一操作只被执行一次。
 * </p>
 */
@Component
@Aspect
@EnableAspectJAutoProxy
public class AntiReplayAspect {

  /**
   * 日志记录器。
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(AntiReplayAspect.class);

  /**
   * 通用的 StringRedisTemplate 实例，用于操作 Redis。
   */
  private StringRedisTemplate commonStringRedisTemplate;

  /**
   * 设置通用的 StringRedisTemplate 实例。
   *
   * @param commonStringRedisTemplate 通用的 StringRedisTemplate 实例。
   */
  @Autowired
  public void setCommonStringRedisTemplate(final StringRedisTemplate commonStringRedisTemplate) {
    this.commonStringRedisTemplate = commonStringRedisTemplate;
  }

  // 扫描所有使用NoRepeat注解修饰的方法
  /**
   * 环绕通知，在标注了 {@link ltd.qubit.commons.annotation.AntiReplay} 注解的方法执行前后进行处理。
   * <p>
   * 该方法会根据请求信息和方法参数生成一个唯一的锁标识。
   * 如果成功获取锁，则执行目标方法；否则，抛出 {@link OperationTooFrequentException} 异常，
   * 表示操作过于频繁或为重复请求。
   * </p>
   *
   * @param joinPoint 代表连接点（即被拦截的方法）的对象。
   * @return 目标方法的执行结果。
   * @throws Throwable 如果目标方法执行过程中发生异常，或者获取锁失败时抛出 {@link OperationTooFrequentException}。
   */
  @Around(value = "@annotation(ltd.qubit.commons.annotation.AntiReplay)")
  public Object annotationAround(final ProceedingJoinPoint joinPoint) throws Throwable {
    final long startTime = System.currentTimeMillis();
    // 获取方法
    final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    // 获取注解
    final AntiReplay antiReplay = method.getAnnotation(AntiReplay.class);
    // 获取请求相关信息
    final RequestAttributes ra = RequestContextHolder.getRequestAttributes();
    final ServletRequestAttributes sra = (ServletRequestAttributes) ra;
    final HttpServletRequest request = sra.getRequest();
    // 根据请求参数生成分布式锁的名称
    String lockStr = antiReplay.clazz().getName() + ":" + method.getName() + ":"
        + request.getMethod() + request.getRequestURI().replaceAll("/", ":");
    if (method.getParameterCount() > 0) {
      // 如果有requestBody注解的请求参数，分布式锁名称需要再处理
      // 找到requestBody注解的请求参数对应的值
      Object requestBodyValue = null;
      final Object[] paramValues = joinPoint.getArgs();
      final Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i++) {
        final Parameter parameter = parameters[i];
        if (!parameter.isAnnotationPresent(RequestBody.class)) {
          continue;
        }
        requestBodyValue = paramValues[i];
      }
      if (requestBodyValue != null) {
        final ObjectMapper objectMapper = new ObjectMapper();
        lockStr += ":" + md5(objectMapper.writeValueAsString(requestBodyValue));
      }
    }
    LOGGER.info("redis key : {}", lockStr);
    final Boolean lock = setRedisLock(lockStr, "1", antiReplay.expireTime(), antiReplay.timeUnit());
    if (lock) {
      LOGGER.info("获取分布式锁成功, 耗时 {}ms，lockName : {}",
          System.currentTimeMillis() - startTime, lockStr);
      return joinPoint.proceed();
    } else {
      LOGGER.info("获取分布式锁失败，判定为重复请求，直接过滤，耗时 {}ms...",
          System.currentTimeMillis() - startTime);
      throw new OperationTooFrequentException("REQUEST", null);
    }
  }

  /**
   * MD5运算。
   *
   * @param content
   *     运算内容
   * @return MD5运算后的Base64编码字符串，如果发生异常则返回null。
   */
  private String md5(final String content) {
    try {
      final MessageDigest md5 = MessageDigest.getInstance("MD5");
      final byte[] bytes = md5.digest(content.getBytes(StandardCharsets.UTF_8));
      return Base64Utils.encodeToString(bytes);
    } catch (final Exception e) {
      LOGGER.error("md5 error", e);
    }
    return null;
  }

  /**
   * 尝试在 Redis 中设置一个锁。
   * <p>
   * 如果键不存在，则设置键值对并设置过期时间；如果键已存在，则不进行任何操作。
   * </p>
   *
   * @param key 锁的键。
   * @param value 锁的值。
   * @param time 过期时间。
   * @param unit 时间单位。
   * @return 如果成功设置锁（即键原先不存在），则返回 {@code true}；否则返回 {@code false}。
   *         如果操作 Redis 出现异常，默认返回 {@code true} 以允许操作继续。
   */
  private Boolean setRedisLock(final String key, final String value, final Long time,
      final TimeUnit unit) {
    try {
      return commonStringRedisTemplate.opsForValue().setIfAbsent(key, value, time, unit);
    } catch (final Exception e) {
      LOGGER.error("set redis lock error", e);
      // 如果操作redis出现了异常，默认返回true
      return true;
    }
  }

}