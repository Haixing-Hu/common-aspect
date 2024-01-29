////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2024.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.common.interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import ltd.qubit.commons.io.IoUtils;

/**
 * A {@link ClientHttpResponse} that buffers the response body.
 *
 * @author Haixing Hu
 */
public class BufferedClientHttpResponse implements ClientHttpResponse {

  private final ClientHttpResponse response;
  private byte[] body;

  public BufferedClientHttpResponse(final ClientHttpResponse response) {
    this.response = response;
  }

  @Override
  @NonNull
  public HttpStatusCode getStatusCode() throws IOException {
    return response.getStatusCode();
  }

  @Override
  @NonNull
  public String getStatusText() throws IOException {
    return response.getStatusText();
  }

  @Override
  public void close() {
    response.close();
  }

  @Override
  @NonNull
  public InputStream getBody() throws IOException {
    if (body == null) {
      body = IoUtils.getBytes(response.getBody(), Integer.MAX_VALUE);
    }
    return new ByteArrayInputStream(body);
  }

  @Override
  @NonNull
  public HttpHeaders getHeaders() {
    return response.getHeaders();
  }
}
