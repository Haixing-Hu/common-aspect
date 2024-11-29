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

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * 一个{@link ServletOutputStream}的实现，它将写入的字节缓冲到一个数组中。
 *
 * @author 胡海星
 */
public class BufferedServletOutputStream extends ServletOutputStream {

  private final ByteArrayOutputStream output;

  private final ServletOutputStream parent;

  public BufferedServletOutputStream(final ByteArrayOutputStream output,
      final ServletOutputStream parent) {
    this.output = output;
    this.parent = parent;
  }

  @Override
  public boolean isReady() {
    return parent.isReady();
  }

  @Override
  public void setWriteListener(final WriteListener writeListener) {
    parent.setWriteListener(writeListener);
  }

  @Override
  public void write(final int b) throws IOException {
    parent.write(b);
    output.write(b);
  }

  @Override
  public void write(final byte[] b) throws IOException {
    parent.write(b);
    output.write(b);
  }

  @Override
  public void write(final byte[] b, final int off, final int len)
      throws IOException {
    parent.write(b, off, len);
    output.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    parent.flush();
  }
}
