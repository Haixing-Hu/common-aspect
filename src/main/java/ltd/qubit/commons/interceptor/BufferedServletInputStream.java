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

  /**
   * 内部的字节数组输入流，由此 {@link ServletInputStream} 读取数据。
   */
  private final ByteArrayInputStream input;

  /**
   * 使用给定的字节数组构造一个 {@code BufferedServletInputStream}。
   *
   * @param buffer 用于提供数据的字节数组。
   */
  public BufferedServletInputStream(final byte[] buffer) {
    this.input = new ByteArrayInputStream(buffer);
  }

  /**
   * 检查是否已读取此 {@code ServletInputStream} 中的所有数据。
   *
   * @return 如果已到达流的末尾，则返回 {@code true}；否则返回 {@code false}。
   */
  @Override
  public boolean isFinished() {
    return input.available() == 0;
  }

  /**
   * 检查此 {@code ServletInputStream} 是否已准备好进行非阻塞读取。
   *
   * @return 总是返回 {@code true}，因为此实现是基于内存缓冲区的，总是可读的。
   */
  @Override
  public boolean isReady() {
    return true;
  }

  /**
   * 设置此 {@code ServletInputStream} 的 {@link ReadListener}。
   * <p>
   * 此方法在此实现中不受支持，将始终抛出 {@link UnsupportedOperationException}。
   * </p>
   *
   * @param readListener 要设置的 {@link ReadListener}。
   * @throws UnsupportedOperationException 始终抛出，因为此操作不受支持。
   */
  @Override
  public void setReadListener(final ReadListener readListener) {
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * 从输入流中读取下一个数据字节。
   *
   * @return 下一个数据字节；如果到达流的末尾，则返回-1。
   */
  @Override
  public int read() {
    return input.read();
  }

  /**
   * 从输入流中读取一些字节并将它们存储到缓冲区数组 {@code b} 中。
   *
   * @param b 目标缓冲区。
   * @return 读入缓冲区的总字节数；如果由于到达流的末尾而没有更多数据，则返回-1。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public int read(final byte[] b) throws IOException {
    return input.read(b);
  }

  /**
   * 从输入流中读取最多 {@code len} 个字节的数据到一个字节数组中。
   *
   * @param b 存储读取数据的缓冲区。
   * @param off 目标数组 {@code b} 中的起始偏移量。
   * @param len 读取的最大字节数。
   * @return 读入缓冲区的总字节数；如果由于到达流的末尾而没有更多数据，则返回-1。
   */
  @Override
  public int read(final byte[] b, final int off, final int len) {
    return input.read(b, off, len);
  }

  /**
   * 从输入流中读取所有剩余字节。
   *
   * @return 包含从此输入流中读取的所有剩余字节的字节数组。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public byte[] readAllBytes() throws IOException {
    return input.readAllBytes();
  }

  /**
   * 从输入流中跳过并丢弃 {@code n} 个数据字节。
   *
   * @param n 要跳过的字节数。
   * @return 实际跳过的字节数。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public long skip(final long n) throws IOException {
    return input.skip(n);
  }

  /**
   * 从输入流中读取所请求数量的字节到新的字节数组中。
   *
   * @param len 要读取的字节数。
   * @return 包含所读取字节的新字节数组。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public byte[] readNBytes(final int len) throws IOException {
    return input.readNBytes(len);
  }

  /**
   * 从输入流中读取字节到给定的字节数组的子数组中。
   *
   * @param b 存储读取数据的缓冲区。
   * @param off 目标数组 {@code b} 中的起始偏移量。
   * @param len 读取的最大字节数。
   * @return 实际读取的字节数。
   */
  @Override
  public int readNBytes(final byte[] b, final int off, final int len) {
    return input.readNBytes(b, off, len);
  }

  /**
   * 从输入流中跳过并丢弃所请求数量的字节。
   *
   * @param n 要跳过的字节数。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public void skipNBytes(final long n) throws IOException {
    input.skipNBytes(n);
  }

  /**
   * 在输入流的当前位置标记。
   *
   * @param readlimit 在标记位置失效前可以读取的最大字节限制。
   */
  @Override
  public synchronized void mark(final int readlimit) {
    input.mark(readlimit);
  }

  /**
   * 将此流重新定位到上次在此输入流上调用 {@code mark} 方法时的位置。
   */
  @Override
  public synchronized void reset() {
    input.reset();
  }

  /**
   * 测试此输入流是否支持 {@code mark} 和 {@code reset} 方法。
   *
   * @return 如果此流实例支持 mark 和 reset 方法，则为 {@code true}；否则为 {@code false}。
   */
  @Override
  public boolean markSupported() {
    return input.markSupported();
  }

  /**
   * 从此输入流中读取所有字节，并按读取顺序将字节写入给定的输出流。
   *
   * @param out 要写入数据的输出流。
   * @return 传输的字节数。
   * @throws IOException 如果发生I/O错误。
   */
  @Override
  public long transferTo(final OutputStream out) throws IOException {
    return input.transferTo(out);
  }
}
