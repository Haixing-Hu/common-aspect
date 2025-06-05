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

  /**
   * 日志记录器。
   */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。默认启用。
   */
  private boolean enabled = true;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private Charset defaultCharset = StandardCharsets.UTF_8;

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
   * 获取默认的字符集。
   *
   * @return 默认字符集。
   */
  public final Charset getDefaultCharset() {
    return defaultCharset;
  }

  /**
   * 设置默认的字符集。
   *
   * @param defaultCharset 要设置的默认字符集。
   */
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
      // traceResponse() 方法会记录响应体，
      // 因此我们需要缓冲响应体以避免响应体被消耗。
      response = new BufferedClientHttpResponse(response);
      traceResponse(response);
    }
    return response;
  }

  /**
   * 追踪并记录HTTP请求的详细信息。
   *
   * @param request HTTP请求对象。
   * @param body 请求体内容的字节数组。
   * @throws IOException 如果读取请求信息时发生I/O错误。
   */
  private void traceRequest(final HttpRequest request, final byte[] body) throws IOException {
    final Charset charset = getCharset(request);
    logger.debug("=========================== request begin ===========================");
    logger.debug("URI         : {}", request.getURI());
    logger.debug("Method      : {}", request.getMethod());
    logger.debug("Headers     : {}", request.getHeaders());
    logger.debug("Request body: {}", new String(body, charset));
    logger.debug("============================ request end ===========================");
  }

  /**
   * 追踪并记录HTTP响应的详细信息。
   *
   * @param response HTTP响应对象，其响应体应该是可重复读取的（例如，通过 {@link BufferedClientHttpResponse} 包装）。
   * @throws IOException 如果读取响应信息时发生I/O错误。
   */
  private void traceResponse(final ClientHttpResponse response) throws IOException {
    final Charset charset = getCharset(response);
    logger.debug("=========================== response begin ===========================");
    logger.debug("Status code  : {}", response.getStatusCode());
    logger.debug("Status text  : {}", response.getStatusText());
    logger.debug("Headers      : {}", response.getHeaders());
    logger.debug("Response body: {}", copyToString(response.getBody(), charset));
    logger.debug("=========================== response end ===========================");
  }

  /**
   * 从HTTP消息（请求或响应）中获取字符集。
   * <p>
   * 它首先尝试从消息的 Content-Type 头部获取字符集。
   * 如果头部中没有指定字符集，则返回配置的 {@link #defaultCharset}。
   * </p>
   *
   * @param message HTTP消息对象，可以是 {@link HttpRequest} 或 {@link ClientHttpResponse}。
   * @return 解析得到的字符集，或者在无法确定时返回默认字符集。
   */
  private Charset getCharset(final HttpMessage message) {
    return Optional.ofNullable(message.getHeaders().getContentType())
                   .map(MediaType::getCharset)
                   .orElse(defaultCharset);
  }
}
