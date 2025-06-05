////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * 一个{@link jakarta.servlet.http.HttpServletResponse}的实现，它包装了另一个
 * {@link jakarta.servlet.http.HttpServletResponse}，
 * 将写入的数据保存到一个内部缓冲区中。然后，提供一个方法来返回这个缓冲区的内容，从而允许
 * 反复读取。
 *
 * @author 胡海星
 */
public class BufferedHttpServletResponse extends HttpServletResponseWrapper {
  /**
   * 存储响应数据的内部字节数组输出流。
   */
  private final ByteArrayOutputStream output;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private final Charset charset;

  /**
   * 使用指定的响应对象和默认的UTF-8字符集构造一个 {@code BufferedHttpServletResponse}。
   *
   * @param response 被包装的原始 {@link jakarta.servlet.http.HttpServletResponse} 对象。
   * @throws IOException 如果在获取父类的输出流时发生I/O错误，虽然在此构造函数中不太可能，但声明以匹配父类。
   */
  public BufferedHttpServletResponse(final jakarta.servlet.http.HttpServletResponse response)
      throws IOException {
    this(response, StandardCharsets.UTF_8);
  }

  /**
   * 使用指定的响应对象和字符集构造一个 {@code BufferedHttpServletResponse}。
   *
   * @param response 被包装的原始 {@link jakarta.servlet.http.HttpServletResponse} 对象。
   * @param charset 用于将缓冲的字节数据转换为字符串的字符集。
   */
  public BufferedHttpServletResponse(final jakarta.servlet.http.HttpServletResponse response,
      final Charset charset) {
    super(response);
    this.output = new ByteArrayOutputStream();
    this.charset = charset;
  }

  /**
   * 获取用于解码响应体的字符集。
   *
   * @return 当前响应使用的字符集。
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * 获取响应体的字节数组副本。
   *
   * @return 包含响应体数据的字节数组。
   */
  public byte[] getBody() {
    return output.toByteArray();
  }

  /**
   * 使用指定的字符集 ({@link #charset}) 将响应体转换为字符串。
   *
   * @return 表示响应体的字符串。
   */
  public String getBodyAsString() {
    return output.toString(charset);
  }

  /**
   * 获取响应中所有头信息的映射。
   * <p>
   * 映射的键是头名称，值是对应头名称的所有值的列表。
   * </p>
   *
   * @return 包含所有响应头及其值的映射。
   */
  public Map<String, List<String>> getHeaders() {
    final Map<String, List<String>> result = new HashMap<>();
    final Collection<String> names = getHeaderNames();
    for (final String name : names) {
      final Collection<String> values = getHeaders(name);
      final List<String> valueList = new ArrayList<>(values);
      result.put(name, valueList);
    }
    return result;
  }

  /**
   * 返回一个 {@link BufferedServletOutputStream} 实例，该实例将数据写入内部缓冲区，
   * 同时也可能写入原始响应的输出流（取决于 {@link BufferedServletOutputStream} 的实现）。
   *
   * @return 一个用于写入二进制响应数据的缓冲 Servlet 输出流。
   * @throws IOException 如果在获取原始响应的输出流时发生I/O错误。
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    final ServletOutputStream parentOutput = super.getOutputStream();
    return new BufferedServletOutputStream(output, parentOutput);
  }

  /**
   * 返回一个 {@link BufferedPrintWriter} 实例，该实例将数据写入内部缓冲区，
   * 同时也可能写入原始响应的写入器（取决于 {@link BufferedPrintWriter} 的实现）。
   *
   * @return 一个用于写入字符文本响应数据的缓冲打印写入器。
   * @throws IOException 如果在获取原始响应的写入器时发生I/O错误。
   */
  @Override
  public PrintWriter getWriter() throws IOException {
    return new BufferedPrintWriter(output, super.getWriter());
  }
}
