////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2022.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.common.interceptor;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CORS 过滤器。
 *
 * @author 胡海星
 */
public class CorsFilter implements Filter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void doFilter(final ServletRequest request,
      final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpResponse.setHeader("Access-Control-Allow-Methods",
        "GET, HEAD, POST, PUT, DELETE, PATCH, OPTIONS");
    httpResponse.setHeader("Access-Control-Allow-Headers",
        "X-Auth-Token, X-Auth-App-Token, X-Auth-User-Token, Content-Type");
    httpResponse.setHeader("Access-Control-Allow-Credentials", "false");
    httpResponse.setHeader("Access-Control-Max-Age", "86400");
    logger.debug("---CORS Configuration Completed---");
    chain.doFilter(request, response);
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
    // empty
  }

  @Override
  public void destroy() {
    //  empty
  }
}
