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

import static ltd.qubit.commons.interceptor.HttpServletUtils.isFileDownload;
import static ltd.qubit.commons.interceptor.HttpServletUtils.isMultipart;
import static ltd.qubit.commons.interceptor.HttpServletUtils.isTextual;

/**
 * 此过滤器用于在日志中打印 RESTful 接口获得的HTTP请求和发送的HTTP响应。
 * <p>
 * <b>注意：</b>此拦可用于配置 Spring Web MVC，如需配置 Spring RestTemplate，
 * 请使用 {@link LoggingClientHttpRequestInterceptor}。
 *
 * @author 胡海星
 */
public class HttpLoggingFilter extends OncePerRequestFilter {

  /**
   * 日志记录器。
   */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled = true;

  /**
   * 是否打印上传内容的文本表示（如果内容是文本类型）。
   */
  private boolean printUploadContent = false;

  /**
   * 是否打印下载内容的文本表示（如果内容是文本类型）。
   */
  private boolean printDownloadContent = false;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private Charset defaultCharset = StandardCharsets.UTF_8;

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
   * 检查是否配置为打印上传内容的文本表示。
   *
   * @return 如果配置为打印上传内容的文本表示，则返回true；否则返回false。
   */
  public boolean isPrintUploadTextualContent() {
    return printUploadContent;
  }

  /**
   * 设置是否打印上传内容的文本表示。
   *
   * @param printUploadTextualContent 如果为true，则打印上传内容的文本表示（如果内容是文本类型）；
   *                                  否则不打印。
   */
  public void setPrintUploadTextualContent(final boolean printUploadTextualContent) {
    this.printUploadContent = printUploadTextualContent;
  }

  /**
   * 检查是否配置为打印下载内容的文本表示。
   *
   * @return 如果配置为打印下载内容的文本表示，则返回true；否则返回false。
   */
  public boolean isPrintDownloadTextualContent() {
    return printDownloadContent;
  }

  /**
   * 设置是否打印下载内容的文本表示。
   *
   * @param printDownloadTextualContent 如果为true，则打印下载内容的文本表示（如果内容是文本类型）；
   *                                  否则不打印。
   */
  public void setPrintDownloadTextualContent(final boolean printDownloadTextualContent) {
    this.printDownloadContent = printDownloadTextualContent;
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

  /**
   * 初始化过滤器Bean。
   * <p>
   * 此方法从过滤器配置中读取 `enabled` 和 `defaultCharset` 初始化参数，
   * 并相应地配置此过滤器实例。
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
    final String defaultCharsetValue = filterConfig.getInitParameter("defaultCharset");
    if (defaultCharsetValue != null) {
      defaultCharset = CharsetUtils.forName(defaultCharsetValue, defaultCharset);
    }
  }

  /**
   * 对HTTP请求和响应进行过滤和日志记录的核心方法。
   * <p>
   * 如果过滤器已启用，此方法会包装原始的请求和响应对象为可缓冲的类型，
   * 然后记录请求详情，接着将请求传递给过滤器链中的下一个过滤器，
   * 最后记录响应详情。
   * 如果过滤器被禁用，则直接将请求和响应传递给下一个过滤器，不进行日志记录。
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

  /**
   * 记录HTTP请求的详细信息。
   *
   * @param request 已缓冲的HTTP请求对象，从中可以读取请求体内容。
   */
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
      if (printUploadContent && isTextual(request)) {
        logger.debug("Request body: {}", request.getBodyAsString());
      } else {
        logger.debug("Request body: {}", "<Ignore the content of uploaded file>");
      }
    } else {
      logger.debug("Request body: {}", request.getBodyAsString());
    }
    logger.debug("============================ request end ============================");
  }

  /**
   * 记录 multipart/form-data 请求的各个部分信息。
   * <p>
   * 此方法会迭代请求中的所有部分 (Part)，并记录每个部分的名称、文件名（如果存在）以及相关的头部信息。
   * 如果在解析过程中发生错误，会记录错误日志。
   * </p>
   *
   * @param request 包含 multipart/form-data 的已缓冲HTTP请求对象。
   */
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

  /**
   * 记录HTTP响应的详细信息。
   *
   * @param response 已缓冲的HTTP响应对象，从中可以读取响应体内容。
   */
  private void loggingResponse(final BufferedHttpServletResponse response) {
    final Map<String, List<String>> headers = response.getHeaders();
    logger.debug("=========================== response begin ===========================");
    logger.debug("Status code  : {}", response.getStatus());
    loggingHeaders(response.getHeaders());
    if (isFileDownload(response)) {
      if (printDownloadContent && isTextual(response)) {
        logger.debug("Response body: {}", response.getBodyAsString());
      } else {
        logger.debug("Response body: {}", "<Ignore the content of file download>");
      }
    } else {
      logger.debug("Response body: {}", response.getBodyAsString());
    }
    logger.debug("============================ response end ============================");
  }

  /**
   * 记录HTTP头部信息。
   *
   * @param headers 包含头部名称到头部值列表映射的Map。
   */
  private void loggingHeaders(final Map<String, List<String>> headers) {
    for (final String key : headers.keySet()) {
      for (final String value: headers.get(key)) {
        logger.debug("Headers     : {} = '{}'", key, value);
      }
    }
  }

  /**
   * 记录HTTP请求参数。
   *
   * @param params 包含参数名称到参数值数组映射的Map。
   */
  private void loggingParameters(final Map<String, String[]> params) {
    for (final String key : params.keySet()) {
      for (final String value: params.get(key)) {
        logger.debug("Params      : {} = '{}'", key, value);
      }
    }
  }

  /**
   * 根据提供的名称获取字符集实例。
   * <p>
   * 如果名称为null，或者根据名称找不到对应的字符集，或者发生其他异常，
   * 则会记录警告日志并返回默认字符集 {@link #defaultCharset}。
   * </p>
   *
   * @param name 字符集的名称，例如 "UTF-8"。
   * @return 对应的 {@link Charset} 实例，或者在无法解析时返回默认字符集。
   */
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
