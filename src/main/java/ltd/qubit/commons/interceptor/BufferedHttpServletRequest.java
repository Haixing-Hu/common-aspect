////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;

import static ltd.qubit.commons.interceptor.HttpServletUtils.isMultipart;
import static ltd.qubit.commons.interceptor.HttpServletUtils.isWwwForm;

/**
 * 一个{@link HttpServletRequest}的实现，它包装了另一个{@link HttpServletRequest}，
 * 将其请求数据流读入一个缓存数组，并在消费者需要时，可以多次重复提供。
 *
 * @author 胡海星
 */
public class BufferedHttpServletRequest extends HttpServletRequestWrapper {

  /**
   * 对于 Multipart 的请求（"multipart/*" ），存储解析出来的 Part
   */
  private final Collection<Part> parts;

  /**
   * 对于 www-form 的请求（"application/x-www-form-urlencoded"），存储解析出来的参数
   */
  private final Map<String, String[]> params;

  /**
   * 存储数据的缓冲区。
   */
  private final byte[] body;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private final Charset charset;

  public BufferedHttpServletRequest(final HttpServletRequest request)
      throws IOException, ServletException {
    this(request, StandardCharsets.UTF_8);
  }

  public BufferedHttpServletRequest(final HttpServletRequest request,
      final Charset charset) throws IOException, ServletException {
    super(request);
    if (isMultipart(request)) {
      this.parts = request.getParts();
      this.params = request.getParameterMap();
      this.body = new byte[0];
    } else if (isWwwForm(request)) {
      this.parts = Collections.emptyList();
      this.params = request.getParameterMap();
      this.body = request.getInputStream().readAllBytes();
    } else {
      this.parts = Collections.emptyList();
      this.params = Collections.emptyMap();
      this.body = request.getInputStream().readAllBytes();
    }
    this.charset = charset;
  }

  @Override
  public Collection<Part> getParts() {
    return parts;
  }

  @Override
  public String getParameter(final String name) {
    final String[] values = getParameterMap().get(name);
    if ((values != null) && (values.length > 0)) {
      return values[0];
    } else {
      return null;
    }
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    if (params.isEmpty()) {
      // 若此对象没缓存到参数，就从父对象中获取
      // FIXME: 这么做是否有必要?
      return super.getParameterMap();
    } else {
      return params;
    }
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.enumeration(getParameterMap().keySet());
  }

  @Override
  public String[] getParameterValues(final String name) {
    return getParameterMap().get(name);
  }

  public byte[] getBody() {
    return body;
  }

  public String getBodyAsString() {
    return new String(body, charset);
  }

  public Charset getCharset() {
    return charset;
  }

  public Map<String, List<String>> getHeaders() {
    final Map<String, List<String>> result = new HashMap<>();
    final Enumeration<String> names = getHeaderNames();
    while (names.hasMoreElements()) {
      final String name = names.nextElement();
      final Enumeration<String> values = getHeaders(name);
      final List<String> valueList = new ArrayList<>();
      while (values.hasMoreElements()) {
        final String value = values.nextElement();
        valueList.add(value);
      }
      result.put(name, valueList);
    }
    return result;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new BufferedServletInputStream(body);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    final InputStream in = new ByteArrayInputStream(body);
    final Reader reader = new InputStreamReader(in, charset);
    return new BufferedReader(reader);
  }
}
