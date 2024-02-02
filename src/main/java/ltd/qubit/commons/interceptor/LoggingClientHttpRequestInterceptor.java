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

import static org.springframework.util.StreamUtils.copyToString;

public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private Charset defaultCharset = StandardCharsets.UTF_8;

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
    traceRequest(request, body);
    final ClientHttpResponse response = execution.execute(request, body);
    // the traceResponse() will log the response body, so we need to buffer the
    // response body to avoid the response body being consumed.
    final BufferedClientHttpResponse bufferedResponse = new BufferedClientHttpResponse(response);
    traceResponse(bufferedResponse);
    return bufferedResponse;
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
