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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.springframework.util.StreamUtils.copyToString;

/**
 * 此拦截器用于在日志中打印RESTful接口获得的HTTP请求和发送的HTTP响应。
 * <p>
 * <b>注意：</b>此拦截器只用于配置 Spring RestTemplate 拦截并打印 RestTemplate
 * 发出的请求和收到的响应，不能配置到 Spring Web MVC中拦截服务器容器收到的请求和发
 * 出的响应。
 * <p>
 * 如需配置Web拦截器，请使用 Spring 提供的 {@link CommonsRequestLoggingFilter}
 * 并在 {@code src/resources/webapp/WEB-INF/web.xml} 中进行配置。
 *
 * @author 胡海星
 * @see CommonsRequestLoggingFilter
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。默认启用。
   */
  private boolean enabled = true;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private Charset defaultCharset = StandardCharsets.UTF_8;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public final Charset getDefaultCharset() {
    return defaultCharset;
  }

  public final void setDefaultCharset(final Charset defaultCharset) {
    this.defaultCharset = defaultCharset;
  }

  @Override
  @NonNull
  public ClientHttpResponse intercept(@NonNull final HttpRequest request,
      @NonNull final byte[] body, final ClientHttpRequestExecution execution)
      throws IOException {
    if (enabled) {
      traceRequest(request, body);
    }
    ClientHttpResponse response = execution.execute(request, body);
    if (enabled) {
      // the traceResponse() will log the response body, so we need to buffer the
      // response body to avoid the response body being consumed.
      response = new BufferedClientHttpResponse(response);
      traceResponse(response);
    }
    return response;
  }

  private void traceRequest(final HttpRequest request, final byte[] body) throws IOException {
    final Charset charset = getCharset(request);
    logger.debug("=========================== request begin ===========================");
    logger.debug("URI         : {}", request.getURI());
    logger.debug("Method      : {}", request.getMethod());
    logger.debug("Headers     : {}", request.getHeaders());
    logger.debug("Request body: {}", new String(body, charset));
    logger.debug("============================ request end ===========================");
  }

  private void traceResponse(final ClientHttpResponse response) throws IOException {
    final Charset charset = getCharset(response);
    logger.debug("=========================== response begin ===========================");
    logger.debug("Status code  : {}", response.getStatusCode());
    logger.debug("Status text  : {}", response.getStatusText());
    logger.debug("Headers      : {}", response.getHeaders());
    logger.debug("Response body: {}", copyToString(response.getBody(), charset));
    logger.debug("=========================== response end ===========================");
  }

  private Charset getCharset(final HttpMessage message) {
    return Optional.ofNullable(message.getHeaders().getContentType())
                   .map(MediaType::getCharset)
                   .orElse(defaultCharset);
  }
}
