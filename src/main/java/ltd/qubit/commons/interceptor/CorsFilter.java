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

  /**
   * 默认允许的来源。
   */
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

  /**
   * 默认是否允许发送凭据。
   */
  public static final String DEFAULT_ALLOW_CREDENTIALS = "false";

  /**
   * 预检请求的默认缓存时间（秒）。
   */
  public static final String DEFAULT_MAX_AGE = "86400";

  /**
   * 默认是否绕过 OPTIONS 请求。
   */
  public static final boolean DEFAULT_BYPASS_OPTIONS_REQUESTS = true;

  /**
   * 日志记录器。
   */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled = true;

  /**
   * 允许的来源。
   */
  private String allowOrigin = DEFAULT_ALLOW_ORIGIN;

  /**
   * 允许的HTTP方法。
   */
  private String allowMethods = DEFAULT_ALLOW_METHODS;

  /**
   * 允许的HTTP请求头。
   */
  private String allowHeaders = DEFAULT_ALLOW_HEADERS;

  /**
   * 暴露给客户端的HTTP响应头。
   */
  private String exposeHeaders = DEFAULT_EXPOSE_HEADERS;

  /**
   * 是否允许发送凭据。
   */
  private String allowCredentials = DEFAULT_ALLOW_CREDENTIALS;

  /**
   * 预检请求的缓存时间（秒）。
   */
  private String maxAge = DEFAULT_MAX_AGE;

  /**
   * 是否绕过 OPTIONS 请求。
   */
  private boolean bypassOptionsRequests = DEFAULT_BYPASS_OPTIONS_REQUESTS;

  /**
   * 检查此过滤器是否启用。
   *
   * @return 如果过滤器启用，则返回true；否则返回false。
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * 设置此过滤器的启用状态。
   *
   * @param enabled 如果为true，则启用过滤器；如果为false，则禁用过滤器。
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * 获取允许的来源。
   *
   * @return 允许的来源字符串。
   */
  public String getAllowOrigin() {
    return allowOrigin;
  }

  /**
   * 设置允许的来源。
   *
   * @param allowOrigin 允许的来源字符串。
   */
  public void setAllowOrigin(final String allowOrigin) {
    this.allowOrigin = allowOrigin;
  }

  /**
   * 获取允许的HTTP方法。
   *
   * @return 允许的HTTP方法字符串。
   */
  public String getAllowMethods() {
    return allowMethods;
  }

  /**
   * 设置允许的HTTP方法。
   *
   * @param allowMethods 允许的HTTP方法字符串。
   */
  public void setAllowMethods(final String allowMethods) {
    this.allowMethods = allowMethods;
  }

  /**
   * 获取允许的HTTP请求头。
   *
   * @return 允许的HTTP请求头字符串。
   */
  public String getAllowHeaders() {
    return allowHeaders;
  }

  /**
   * 设置允许的HTTP请求头。
   *
   * @param allowHeaders 允许的HTTP请求头字符串。
   */
  public void setAllowHeaders(final String allowHeaders) {
    this.allowHeaders = allowHeaders;
  }

  /**
   * 获取暴露给客户端的HTTP响应头。
   *
   * @return 暴露给客户端的HTTP响应头字符串。
   */
  public String getExposeHeaders() {
    return exposeHeaders;
  }

  /**
   * 设置暴露给客户端的HTTP响应头。
   *
   * @param exposeHeaders 暴露给客户端的HTTP响应头字符串。
   */
  public void setExposeHeaders(final String exposeHeaders) {
    this.exposeHeaders = exposeHeaders;
  }

  /**
   * 获取是否允许发送凭据的设置。
   *
   * @return 是否允许发送凭据的字符串（"true" 或 "false"）。
   */
  public String getAllowCredentials() {
    return allowCredentials;
  }

  /**
   * 设置是否允许发送凭据。
   *
   * @param allowCredentials 是否允许发送凭据的字符串（"true" 或 "false"）。
   */
  public void setAllowCredentials(final String allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  /**
   * 获取预检请求的缓存时间。
   *
   * @return 预检请求的缓存时间字符串（秒）。
   */
  public String getMaxAge() {
    return maxAge;
  }

  /**
   * 设置预检请求的缓存时间。
   *
   * @param maxAge 预检请求的缓存时间字符串（秒）。
   */
  public void setMaxAge(final String maxAge) {
    this.maxAge = maxAge;
  }

  /**
   * 检查是否配置为绕过OPTIONS请求。
   *
   * @return 如果配置为绕过OPTIONS请求，则返回true；否则返回false。
   */
  public boolean isBypassOptionsRequests() {
    return bypassOptionsRequests;
  }

  /**
   * 设置是否绕过OPTIONS请求。
   *
   * @param bypassOptionsRequests 如果为true，则绕过OPTIONS请求；否则不绕过。
   */
  public void setBypassOptionsRequests(final boolean bypassOptionsRequests) {
    this.bypassOptionsRequests = bypassOptionsRequests;
  }

  /**
   * 初始化过滤器Bean。
   * <p>
   * 此方法从过滤器配置中读取CORS相关的初始化参数，并相应地配置此过滤器实例。
   * </p>
   */
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

  /**
   * 对HTTP请求和响应进行CORS处理的核心方法。
   * <p>
   * 如果过滤器已启用，此方法会根据配置设置相应的CORS响应头。
   * 如果配置为绕过OPTIONS请求，并且当前请求是OPTIONS请求，则直接返回200 OK。
   * 否则，将请求传递给过滤器链中的下一个过滤器。
   * 如果过滤器被禁用，则直接将请求和响应传递给下一个过滤器，不进行CORS处理。
   * </p>
   *
   * @param request 当前的HTTP请求。
   * @param response 当前的HTTP响应。
   * @param filterChain 过滤器链。
   * @throws ServletException 如果在处理请求或响应时发生Servlet相关异常。
   * @throws IOException 如果在处理请求或响应时发生I/O异常。
   */
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
