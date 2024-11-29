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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * 一个{@link HttpServletResponse}的实现，它包装了另一个{@link HttpServletResponse}，
 * 将写入的数据保存到一个内部缓冲区中。然后，提供一个方法来返回这个缓冲区的内容，从而允许
 * 反复读取。
 *
 * @author 胡海星
 */
public class BufferedHttpServletResponse extends HttpServletResponseWrapper {
  /**
   * 存储数据的缓冲区。
   */
  private final ByteArrayOutputStream output;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private final Charset charset;

  public BufferedHttpServletResponse(final HttpServletResponse response)
      throws IOException {
    this(response, StandardCharsets.UTF_8);
  }

  public BufferedHttpServletResponse(final HttpServletResponse response,
      final Charset charset) {
    super(response);
    this.output = new ByteArrayOutputStream();
    this.charset = charset;
  }

  public Charset getCharset() {
    return charset;
  }

  public byte[] getBody() {
    return output.toByteArray();
  }

  public String getBodyAsString() {
    return output.toString(charset);
  }

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

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    final ServletOutputStream parentOutput = super.getOutputStream();
    return new BufferedServletOutputStream(output, parentOutput);
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    return new BufferedPrintWriter(output, super.getWriter());
  }
}
