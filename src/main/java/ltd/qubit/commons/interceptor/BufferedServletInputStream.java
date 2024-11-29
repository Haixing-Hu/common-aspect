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
import java.io.OutputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * 一个{@link ServletInputStream}的实现，它将读取给定的字节数组中的数据。
 *
 * @author 胡海星
 */
public class BufferedServletInputStream extends ServletInputStream {

  private final ByteArrayInputStream input;

  public BufferedServletInputStream(final byte[] buffer) {
    this.input = new ByteArrayInputStream(buffer);
  }

  @Override
  public boolean isFinished() {
    return input.available() == 0;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setReadListener(final ReadListener readListener) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public int read() {
    return input.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return input.read(b);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) {
    return input.read(b, off, len);
  }

  @Override
  public byte[] readAllBytes() throws IOException {
    return input.readAllBytes();
  }

  @Override
  public long skip(final long n) throws IOException {
    return input.skip(n);
  }

  @Override
  public byte[] readNBytes(final int len) throws IOException {
    return input.readNBytes(len);
  }

  @Override
  public int readNBytes(final byte[] b, final int off, final int len) {
    return input.readNBytes(b, off, len);
  }

  @Override
  public void skipNBytes(final long n) throws IOException {
    input.skipNBytes(n);
  }

  @Override
  public synchronized void mark(final int readlimit) {
    input.mark(readlimit);
  }

  @Override
  public synchronized void reset() {
    input.reset();
  }

  @Override
  public boolean markSupported() {
    return input.markSupported();
  }

  @Override
  public long transferTo(final OutputStream out) throws IOException {
    return input.transferTo(out);
  }
}
