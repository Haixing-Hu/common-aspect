////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2022.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.common.interceptor;

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
public class ExecutionTimeInterceptor implements HandlerInterceptor {

  private static final String START_TIME = "start-time";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public boolean preHandle(final HttpServletRequest request,
          final HttpServletResponse response, final Object handler) {
    logger.debug("Executing request: {} - {}", request.getMethod(), request.getRequestURI());
    final long startTime = System.currentTimeMillis();
    request.setAttribute(START_TIME, startTime);
    return true;
  }

  @Override
  public void postHandle(final HttpServletRequest request,
          final HttpServletResponse response, final Object handler,
          final ModelAndView modelAndView) {
    final long startTime = (Long) request.getAttribute(START_TIME);
    final long endTime = System.currentTimeMillis();
    final long duration = endTime - startTime;
    logger.debug("Executing the request: {} - {}, finished in {} milliseconds.",
            request.getMethod(), request.getRequestURI(), duration);
  }
}
