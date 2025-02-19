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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import ltd.qubit.commons.text.CharsetUtils;

import static ltd.qubit.commons.interceptor.HttpServletUtils.isBinary;
import static ltd.qubit.commons.interceptor.HttpServletUtils.isFileDownload;
import static ltd.qubit.commons.interceptor.HttpServletUtils.isMultipart;

/**
 * 此过滤器用于在日志中打印 RESTful 接口获得的HTTP请求和发送的HTTP响应。
 * <p>
 * <b>注意：</b>此拦可用于配置 Spring Web MVC，如需配置 Spring RestTemplate，
 * 请使用 {@link LoggingClientHttpRequestInterceptor}。
 *
 * @author 胡海星
 */
public class HttpLoggingFilter extends OncePerRequestFilter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled = true;

  private boolean printMultipartContent = false;

  private boolean printTextFileDownloadContent = false;

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

  public boolean isPrintMultipartContent() {
    return printMultipartContent;
  }

  public void setPrintMultipartContent(final boolean printMultipartContent) {
    this.printMultipartContent = printMultipartContent;
  }

  public boolean isPrintTextFileDownloadContent() {
    return printTextFileDownloadContent;
  }

  public void setPrintTextFileDownloadContent(final boolean printTextFileDownloadContent) {
    this.printTextFileDownloadContent = printTextFileDownloadContent;
  }

  public final Charset getDefaultCharset() {
    return defaultCharset;
  }

  public final void setDefaultCharset(final Charset defaultCharset) {
    this.defaultCharset = defaultCharset;
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
    final String defaultCharsetValue = filterConfig.getInitParameter("defaultCharset");
    if (defaultCharsetValue != null) {
      defaultCharset = CharsetUtils.forName(defaultCharsetValue, defaultCharset);
    }
  }

  @Override
  protected void doFilterInternal(@Nonnull final HttpServletRequest request,
      @Nonnull final jakarta.servlet.http.HttpServletResponse response,
      @Nonnull final FilterChain filterChain) throws ServletException, IOException {
    if (enabled) {
      final String charsetName = request.getCharacterEncoding();
      final Charset charset = getCharsetImpl(charsetName);
      final BufferedHttpServletRequest bufferedRequest
          = new BufferedHttpServletRequest(request, charset);
      final BufferedHttpServletResponse bufferedResponse
          = new BufferedHttpServletResponse(response, charset);
      loggingRequest(bufferedRequest);
      filterChain.doFilter(bufferedRequest, bufferedResponse);
      loggingResponse(bufferedResponse);
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private void loggingRequest(final BufferedHttpServletRequest request) {
    logger.debug("=========================== request begin ===========================");
    logger.debug("URI         : {}", request.getRequestURI());
    logger.debug("Method      : {}", request.getMethod());
    logger.debug("Remote IP   : {}", request.getRemoteAddr());
    logger.debug("Content-Type: {}", request.getContentType());
    loggingParameters(request.getParameterMap());
    loggingHeaders(request.getHeaders());
    if (isMultipart(request)) {
      // 不打印上传文件的二进制内容
      loggingMultipart(request);
      if (printMultipartContent) {
        logger.debug("Request body: {}", request.getBodyAsString());
      } else {
        logger.debug("Request body: {}", "<Ignore the content of uploaded file>");
      }
    } else {
      logger.debug("Request body: {}", request.getBodyAsString());
    }
    logger.debug("============================ request end ============================");
  }

  private void loggingMultipart(final BufferedHttpServletRequest request) {
    try {
      final Collection<Part> parts = request.getParts();
      logger.debug("part count   : '{}'", parts.size());
      for (final Part part : parts) {
        final String name = part.getName();
        final String headerValue = part.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        final ContentDisposition disposition = ContentDisposition.parse(headerValue);
        final String filename = disposition.getFilename();
        logger.debug("--------------------------- part begin ---------------------------");
        logger.debug("part name    : '{}'", name);
        logger.debug("part filename: '{}'", filename);
        logger.debug("part header  : '{}'", headerValue);
        logger.debug("--------------------------- part end ---------------------------");
      }
    } catch (final Throwable ex) {
      logger.error("Failed to parse the multipart request.", ex);
    }
  }

  private void loggingResponse(final BufferedHttpServletResponse response) {
    final Map<String, List<String>> headers = response.getHeaders();
    logger.debug("=========================== response begin ===========================");
    logger.debug("Status code  : {}", response.getStatus());
    loggingHeaders(response.getHeaders());
    if (isFileDownload(response)) {
      if (isBinary(response) || (!printTextFileDownloadContent)) {
        logger.debug("Response body: {}", "<Ignore the content of file download>");
      } else {
        logger.debug("Response body: {}", response.getBodyAsString());
      }
    } else {
      logger.debug("Response body: {}", response.getBodyAsString());
    }
    logger.debug("============================ response end ============================");
  }

  private void loggingHeaders(final Map<String, List<String>> headers) {
    for (final String key : headers.keySet()) {
      for (final String value: headers.get(key)) {
        logger.debug("Headers     : {} = '{}'", key, value);
      }
    }
  }

  private void loggingParameters(final Map<String, String[]> params) {
    for (final String key : params.keySet()) {
      for (final String value: params.get(key)) {
        logger.debug("Params      : {} = '{}'", key, value);
      }
    }
  }

  private Charset getCharsetImpl(final String name) {
    if (name == null) {
      logger.warn("No character encoding for the HTTP servlet request, "
          + "use the default charset encoding: {}", defaultCharset.name());
      return defaultCharset;
    } else {
      try {
        return Charset.forName(name);
      } catch (final Exception e) {
        logger.warn("Illegal character encoding for the HTTP servlet request: '{}',"
            + "use the default charset encoding: {}", name, defaultCharset.name());
        return defaultCharset;
      }
    }
  }
}
