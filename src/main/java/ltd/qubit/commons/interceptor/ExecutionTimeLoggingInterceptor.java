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
 * The interceptor used to logging the execution time of requests.
 *
 * @author Haixing Hu
 */
public class ExecutionTimeLoggingInterceptor implements HandlerInterceptor {

  private static final String START_TIME = "start-time";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

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
