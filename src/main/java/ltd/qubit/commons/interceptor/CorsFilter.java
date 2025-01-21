////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.io.IOException;

import javax.annotation.Nonnull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * CORS 过滤器。
 *
 * @author 胡海星
 */
public class CorsFilter extends OncePerRequestFilter {

  public static final String DEFAULT_ALLOW_ORIGIN = "*";

  /**
   * 默认的跨域请求方法。
   * <p>
   * {@code Access-Control-Allow-Methods} 响应头控制哪些请求方法能在跨域请求中使用。
   */
  public static final String DEFAULT_ALLOW_METHODS = "GET, HEAD, POST, PUT, DELETE, PATCH, OPTIONS";

  /**
   * 默认的跨域请求头。
   * <p>
   * {@code Access-Control-Allow-Headers} 响应头控制哪些请求头能在跨域请求中使用。
   * <p>
   * 常见的如下
   * <ul>
   *   <li>Authorization: 用于身份验证。</li>
   *   <li>Content-Type: 用于指定请求体的类型。</li>
   *   <li>X-Auth-Token: 用于身份验证。</li>
   *   <li>X-Auth-App-Token: 用于应用程序身份验证。</li>
   *   <li>X-Auth-User-Token: 用于用户身份验证。</li>
   * </ul>
   */
  public static final String DEFAULT_ALLOW_HEADERS = "Authorization, Content-Type, X-Auth-Token, X-Auth-App-Token, X-Auth-User-Token";

  /**
   * 默认的跨域暴露响应头。
   * <p>
   * {@code Access-Control-Expose-Headers} 响应头 控制前端能从跨域响应中访问哪些非默认的响应头。
   * <p>
   * 常见的如下：
   * <ul>
   * <li>Content-Disposition: 文件下载时的文件名。</li>
   * <li>ETag: 用于缓存和资源版本管理。</li>
   * <li>Content-Range: 分段下载时返回的范围。</li>
   * <li>X-RateLimit-Limit 和 X-RateLimit-Remaining: API 调用速率限制相关信息。</li>
   * <li>Retry-After: 指定重试请求的等待时间。</li>
   * </ul>
   */
  public static final String DEFAULT_EXPOSE_HEADERS = "Content-Disposition, Content-Range, ETag, X-RateLimit-Limit, X-RateLimit-Remaining, Retry-After";

  public static final String DEFAULT_ALLOW_CREDENTIALS = "false";

  public static final String DEFAULT_MAX_AGE = "86400";

  public static final boolean DEFAULT_BYPASS_OPTIONS_REQUESTS = true;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled = true;

  private String allowOrigin = DEFAULT_ALLOW_ORIGIN;

  private String allowMethods = DEFAULT_ALLOW_METHODS;

  private String allowHeaders = DEFAULT_ALLOW_HEADERS;

  private String exposeHeaders = DEFAULT_EXPOSE_HEADERS;

  private String allowCredentials = DEFAULT_ALLOW_CREDENTIALS;

  private String maxAge = DEFAULT_MAX_AGE;

  private boolean bypassOptionsRequests = DEFAULT_BYPASS_OPTIONS_REQUESTS;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public String getAllowOrigin() {
    return allowOrigin;
  }

  public void setAllowOrigin(final String allowOrigin) {
    this.allowOrigin = allowOrigin;
  }

  public String getAllowMethods() {
    return allowMethods;
  }

  public void setAllowMethods(final String allowMethods) {
    this.allowMethods = allowMethods;
  }

  public String getAllowHeaders() {
    return allowHeaders;
  }

  public void setAllowHeaders(final String allowHeaders) {
    this.allowHeaders = allowHeaders;
  }

  public String getExposeHeaders() {
    return exposeHeaders;
  }

  public void setExposeHeaders(final String exposeHeaders) {
    this.exposeHeaders = exposeHeaders;
  }

  public String getAllowCredentials() {
    return allowCredentials;
  }

  public void setAllowCredentials(final String allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  public String getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(final String maxAge) {
    this.maxAge = maxAge;
  }

  public boolean isBypassOptionsRequests() {
    return bypassOptionsRequests;
  }

  public void setBypassOptionsRequests(final boolean bypassOptionsRequests) {
    this.bypassOptionsRequests = bypassOptionsRequests;
  }

  @Override
  protected void initFilterBean() {
    final FilterConfig filterConfig = getFilterConfig();
    if (filterConfig == null) {
      return;
    }
    final String enabledValue = filterConfig.getInitParameter("enabled");
    if (enabledValue != null) {
      enabled = "true".equals(enabledValue);
    }
    final String allowOriginValue = filterConfig.getInitParameter("allowOrigin");
    if (allowOriginValue != null) {
      allowOrigin = allowOriginValue;
    }
    final String allowMethodsValue = filterConfig.getInitParameter("allowMethods");
    if (allowMethodsValue != null) {
      allowMethods = allowMethodsValue;
    }
    final String allowHeadersValue = filterConfig.getInitParameter("allowHeaders");
    if (allowHeadersValue != null) {
      allowHeaders = allowHeadersValue;
    }
    final String exposeHeadersValue = filterConfig.getInitParameter("exposeHeaders");
    if (exposeHeadersValue != null) {
      exposeHeaders = exposeHeadersValue;
    }
    final String allowCredentialsValue = filterConfig.getInitParameter("allowCredentials");
    if (allowCredentialsValue != null) {
      allowCredentials = allowCredentialsValue;
    }
    final String maxAgeValue = filterConfig.getInitParameter("maxAge");
    if (maxAgeValue != null) {
      maxAge = maxAgeValue;
    }
    final String bypassOptionsRequestsValue = filterConfig.getInitParameter("bypassOptionsRequests");
    if (bypassOptionsRequestsValue != null) {
      bypassOptionsRequests = "true".equals(bypassOptionsRequestsValue);
    }
  }

  @Override
  protected void doFilterInternal(@Nonnull final HttpServletRequest request,
      @Nonnull final HttpServletResponse response,
      @Nonnull final FilterChain filterChain) throws ServletException, IOException {
    if (enabled) {
      logger.debug("--- CORS Configuration Started ---");
      logger.debug("Setting Access-Control-Allow-Origin: {}", allowOrigin);
      response.setHeader("Access-Control-Allow-Origin", allowOrigin);
      logger.debug("Setting Access-Control-Allow-Methods: {}", allowMethods);
      response.setHeader("Access-Control-Allow-Methods", allowMethods);
      logger.debug("Setting Access-Control-Allow-Headers: {}", allowHeaders);
      response.setHeader("Access-Control-Allow-Headers", allowHeaders);
      logger.debug("Setting Access-Control-Expose-Headers: {}", exposeHeaders);
      response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
      logger.debug("Setting Access-Control-Allow-Credentials: {}", allowCredentials);
      response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
      logger.debug("Setting Access-Control-Max-Age: {}", maxAge);
      response.setHeader("Access-Control-Max-Age", maxAge);
      logger.debug("--- CORS Configuration Completed ---");
      if (bypassOptionsRequests) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
          logger.debug("Bypassing the OPTIONS request: {}", request);
          response.setStatus(HttpServletResponse.SC_OK);
          return;
        }
      }
      // 对于非OPTIONS请求，继续请求的处理
      filterChain.doFilter(request, response);
    } else {
      logger.debug("CorsFilter is disabled, bypassing it.");
      filterChain.doFilter(request, response);
    }
  }
}
