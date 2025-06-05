////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import javax.annotation.Nonnull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用于记录请求执行时间的拦截器。
 *
 * @author 胡海星
 */
public class ExecutionTimeLoggingInterceptor implements HandlerInterceptor {

  /**
   * 用于在请求属性中存储开始时间的键。
   */
  private static final String START_TIME = "start-time";

  /**
   * 日志记录器。
   */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled;

  /**
   * 检查此拦截器是否启用。
   *
   * @return 如果拦截器启用，则返回true；否则返回false。
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * 设置此拦截器的启用状态。
   *
   * @param enabled 如果为true，则启用拦截器；如果为false，则禁用拦截器。
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * 在处理程序执行之前调用。
   * <p>
   * 如果拦截器已启用，则记录请求的开始，并在请求属性中存储当前时间戳。
   * </p>
   *
   * @param request 当前的HTTP请求。
   * @param response 当前的HTTP响应。
   * @param handler 选择的处理程序来处理此请求。
   * @return 总是返回 {@code true} 以继续处理链中的下一个拦截器或处理程序本身。
   */
  @Override
  public boolean preHandle(@Nonnull final HttpServletRequest request,
      @Nonnull final HttpServletResponse response,
      @Nonnull final Object handler) {
    if (enabled) {
      logger.debug("Executing request: {} - {}", request.getMethod(), request.getRequestURI());
      final long startTime = System.currentTimeMillis();
      request.setAttribute(START_TIME, startTime);
    }
    return true;
  }

  /**
   * 在处理程序执行之后，但在视图呈现之前调用。
   * <p>
   * 如果拦截器已启用，则从请求属性中检索开始时间，计算执行持续时间，并记录请求的完成情况及耗时。
   * </p>
   *
   * @param request 当前的HTTP请求。
   * @param response 当前的HTTP响应。
   * @param handler 选择的处理程序。
   * @param modelAndView 处理程序返回的 {@link ModelAndView}（可能为null）。
   */
  @Override
  public void postHandle(@Nonnull final HttpServletRequest request,
      @Nonnull final HttpServletResponse response,
      @Nonnull final Object handler,
      final ModelAndView modelAndView) {
    if (enabled) {
      final long startTime = (Long) request.getAttribute(START_TIME);
      final long endTime = System.currentTimeMillis();
      final long duration = endTime - startTime;
      logger.debug("Executing the request: {} - {}, finished in {} milliseconds.",
          request.getMethod(), request.getRequestURI(), duration);
    }
  }
}