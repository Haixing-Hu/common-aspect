////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////

package ltd.qubit.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 防重放攻击注解。
 * <p>
 * 此注解用于标记需要防止重放攻击的方法。当一个方法被此注解标记后，
 * {@link ltd.qubit.commons.aspect.AntiReplayAspect} 切面会拦截对此方法的调用。
 * 切面会根据方法的类、名称、请求URI、HTTP方法以及请求体（如果存在）生成一个唯一的键，
 * 并尝试在 Redis 中设置一个带有过期时间的锁。如果锁成功设置，则方法会正常执行；
 * 如果锁已存在（表示在过期时间内已有相同请求），则会抛出
 * {@link ltd.qubit.commons.error.OperationTooFrequentException} 异常，从而阻止重复执行。
 * </p>
 * <p>
 * 使用场景：适用于需要确保在一定时间内只执行一次的操作，例如防止用户快速重复提交表单。
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AntiReplay {

  /**
   * 指定与防重放逻辑相关的类。
   * <p>
   * 此值通常是被注解方法所在的类，用于构建 Redis 锁的键，以确保键的唯一性。
   * </p>
   *
   * @return 与此防重放规则关联的类。
   */
  Class<?> clazz();

  /**
   * 锁的过期时间。
   * <p>
   * 定义了在 Redis 中设置的锁的有效持续时间。在此时间段内，相同的请求将被视为重放攻击并被阻止。
   * 时间单位由 {@link #timeUnit()} 指定。
   * </p>
   *
   * @return 锁的过期时间值。
   */
  long expireTime();

  /**
   * {@link #expireTime()} 的时间单位。
   * <p>
   * 默认为 {@link TimeUnit#SECONDS}。
   * </p>
   *
   * @return 过期时间的时间单位。
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

}
