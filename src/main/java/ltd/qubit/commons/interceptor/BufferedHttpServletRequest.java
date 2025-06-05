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
   * 存储请求体数据的内部字节数组缓冲区。
   * 对于 multipart 或 www-form 类型的请求，此缓冲区可能为空或部分填充，
   * 因为这些类型请求的数据主要通过 parts 和 params 字段处理。
   */
  private final byte[] body;

  /**
   * 拦截器解码HTTP请求和响应的body内容时，默认使用的字符集。
   */
  private final Charset charset;

  /**
   * 使用指定的请求对象和默认的UTF-8字符集构造一个 {@code BufferedHttpServletRequest}。
   *
   * @param request 被包装的原始 {@link HttpServletRequest} 对象。
   * @throws IOException 如果在读取请求输入流时发生I/O错误。
   * @throws ServletException 如果在解析 multipart 请求时发生Servlet相关错误。
   */
  public BufferedHttpServletRequest(final HttpServletRequest request)
      throws IOException, ServletException {
    this(request, StandardCharsets.UTF_8);
  }

  /**
   * 使用指定的请求对象和字符集构造一个 {@code BufferedHttpServletRequest}。
   * <p>
   * 此构造函数会根据请求的 Content-Type 处理请求体：
   * <ul>
   *   <li>如果是 multipart 请求，则解析 parts 和 parameters，请求体 {@link #body} 设置为空数组。</li>
   *   <li>如果是 www-form 请求，则解析 parameters，并将输入流内容读入 {@link #body}。</li>
   *   <li>对于其他类型的请求，输入流内容将直接读入 {@link #body}。</li>
   * </ul>
   * </p>
   *
   * @param request 被包装的原始 {@link HttpServletRequest} 对象。
   * @param charset 用于解码请求体的字符集，主要在 {@link #getBodyAsString()} 中使用。
   * @throws IOException 如果在读取请求输入流时发生I/O错误。
   * @throws ServletException 如果在解析 multipart 请求时发生Servlet相关错误。
   */
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

  /**
   * 获取此请求中包含的所有 {@link Part} 对象。
   * <p>
   * 如果原始请求不是 multipart 类型，则返回空集合。
   * </p>
   *
   * @return 此请求中所有 {@link Part} 对象的集合，如果不是 multipart 请求则为空集合。
   */
  @Override
  public Collection<Part> getParts() {
    return parts;
  }

  /**
   * 返回具有给定名称的请求参数的值；如果参数不存在，则返回 {@code null}。
   * <p>
   * 此方法从缓存的参数映射中检索值。
   * </p>
   *
   * @param name 指定参数名称的 {@code String}。
   * @return 包含参数值的 {@code String}，或如果参数不存在则为 {@code null}。
   */
  @Override
  public String getParameter(final String name) {
    final String[] values = getParameterMap().get(name);
    if ((values != null) && (values.length > 0)) {
      return values[0];
    } else {
      return null;
    }
  }

  /**
   * 返回此请求参数的 {@code java.util.Map}。
   * <p>
   * 如果在构造时解析并缓存了参数（例如对于 multipart 或 www-form 请求），则返回缓存的参数映射。
   * 否则，它会委托给包装的请求对象来获取参数映射。
   * 注意：当前实现中，如果内部参数映射为空，则会尝试从父请求获取，这种行为的必要性待确认 (FIXME)。
   * </p>
   *
   * @return 一个不可变的 {@code java.util.Map}，其中包含参数名作为键，参数值数组作为值。
   */
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

  /**
   * 返回此请求中包含的参数名称的 {@code Enumeration}。
   * <p>
   * 此枚举基于 {@link #getParameterMap()} 返回的参数名称。
   * </p>
   *
   * @return 此请求中参数名称的 {@code Enumeration}。
   */
  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.enumeration(getParameterMap().keySet());
  }

  /**
   * 返回一个 {@code String} 数组，其中包含具有给定名称的请求参数的所有值；如果参数不存在，则返回 {@code null}。
   * <p>
   * 此方法从缓存的参数映射中检索值。
   * </p>
   *
   * @param name 指定参数名称的 {@code String}。
   * @return 包含参数值的 {@code String} 数组，或如果参数不存在则为 {@code null}。
   */
  @Override
  public String[] getParameterValues(final String name) {
    return getParameterMap().get(name);
  }

  /**
   * 获取请求体的字节数组副本。
   * <p>
   * 对于非 multipart 和非 www-form 请求，这将是完整的请求体。
   * 对于 multipart 或 www-form 请求，此方法可能返回空数组或部分数据，
   * 因为这些请求的主要内容通过 {@link #getParts()} 和 {@link #getParameterMap()} 访问。
   * </p>
   *
   * @return 包含请求体数据的字节数组。
   */
  public byte[] getBody() {
    return body;
  }

  /**
   * 使用指定的字符集 ({@link #charset}) 将请求体转换为字符串。
   *
   * @return 表示请求体的字符串。
   * @see #getBody()
   */
  public String getBodyAsString() {
    return new String(body, charset);
  }

  /**
   * 获取用于解码请求体的字符集。
   *
   * @return 当前请求使用的字符集。
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * 获取请求中所有头信息的映射。
   * <p>
   * 映射的键是头名称，值是对应头名称的所有值的列表。
   * </p>
   *
   * @return 包含所有请求头及其值的映射。
   */
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

  /**
   * 返回一个 {@link ServletInputStream}，用于读取此请求的基于内部缓冲区的请求体。
   * <p>
   * 多次调用此方法将返回引用相同缓冲数据的流。
   * </p>
   *
   * @return 一个用于读取请求体的 {@link ServletInputStream}。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new BufferedServletInputStream(body);
  }

  /**
   * 返回一个 {@link BufferedReader}，用于使用 {@link #charset} 字符集读取此请求的基于内部缓冲区的请求体。
   * <p>
   * 多次调用此方法将返回引用相同缓冲数据的读取器。
   * </p>
   *
   * @return 一个用于读取请求体的 {@link BufferedReader}。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public BufferedReader getReader() throws IOException {
    final InputStream in = new ByteArrayInputStream(body);
    final Reader reader = new InputStreamReader(in, charset);
    return new BufferedReader(reader);
  }
}
