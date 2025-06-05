////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import ltd.qubit.commons.io.IoUtils;

/**
 * 一个 {@link ClientHttpResponse} 的实现，它会缓冲响应体，
 * 从而允许响应体被多次读取。
 *
 * @author 胡海星
 */
public class BufferedClientHttpResponse implements ClientHttpResponse {

  /**
   * 被包装的原始 {@link ClientHttpResponse} 实例。
   * 所有非 {@code getBody()} 的操作都将委托给此实例。
   */
  private final ClientHttpResponse response;
  /**
   * 用于存储响应体字节的内部缓冲区。
   * <p>
   * 此字段在 {@link #getBody()} 方法首次被调用时，通过读取原始 {@link #response} 的完整响应体来填充。
   * 一旦填充，后续对 {@link #getBody()} 的调用将从此缓冲区提供数据。
   * </p>
   */
  private byte[] body;

  /**
   * 构造一个 {@code BufferedClientHttpResponse} 实例。
   *
   * @param response 被包装的原始 {@link ClientHttpResponse} 对象。
   *                 <b>重要：</b>为了确保响应体能被正确缓冲，不应在调用此包装器的 {@link #getBody()} 方法之前，
   *                 从此原始 {@code response} 对象中读取或消耗其响应体。
   */
  public BufferedClientHttpResponse(final ClientHttpResponse response) {
    this.response = response;
  }

  /**
   * 获取响应的HTTP状态码，委托给原始响应。
   *
   * @return HTTP状态码。
   * @throws IOException 如果在从原始响应获取状态码时发生I/O错误。
   */
  @Override
  @Nonnull
  public HttpStatusCode getStatusCode() throws IOException {
    return response.getStatusCode();
  }

  /**
   * 获取响应的HTTP状态文本，委托给原始响应。
   *
   * @return HTTP状态文本。
   * @throws IOException 如果在从原始响应获取状态文本时发生I/O错误。
   */
  @Override
  @Nonnull
  public String getStatusText() throws IOException {
    return response.getStatusText();
  }

  /**
   * 关闭原始的 {@link ClientHttpResponse}。
   */
  @Override
  public void close() {
    response.close();
  }

  /**
   * 获取响应体作为 {@link InputStream}。
   * <p>
   * 首次调用此方法时，它会从原始响应 ({@link #response}) 中读取整个响应体，
   * 并将其内容存储在内部的字节数组 ({@link #body}) 中。
   * 后续对此方法的调用将返回一个新的 {@link ByteArrayInputStream}，该流从内部缓冲的字节数组中读取数据。
   * 这样可以确保响应体可以被多次读取。
   * </p>
   *
   * @return 包含响应体的输入流。
   * @throws IOException 如果在首次读取原始响应体时发生I/O错误。
   */
  @Override
  @Nonnull
  public InputStream getBody() throws IOException {
    if (body == null) {
      body = IoUtils.getBytes(response.getBody(), Integer.MAX_VALUE);
    }
    return new ByteArrayInputStream(body);
  }

  /**
   * 获取响应的HTTP头信息，委托给原始响应。
   *
   * @return HTTP头信息。
   */
  @Override
  @Nonnull
  public HttpHeaders getHeaders() {
    return response.getHeaders();
  }
}