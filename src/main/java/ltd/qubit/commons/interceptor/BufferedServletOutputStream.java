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
 * 一个 {@link ServletOutputStream} 的实现，它将所有写入操作同时委托给一个
 * 内部的 {@link ByteArrayOutputStream} (用于缓冲) 和一个原始的父 {@link ServletOutputStream}。
 *
 * @author 胡海星
 */
public class BufferedServletOutputStream extends ServletOutputStream {

  /**
   * 用于缓冲写入数据的内部字节数组输出流。
   */
  private final ByteArrayOutputStream output;

  /**
   * 原始的父 {@link ServletOutputStream}，写入操作也会被委托给它。
   */
  private final ServletOutputStream parent;

  /**
   * 构造一个 {@code BufferedServletOutputStream} 实例。
   *
   * @param output 用于缓冲写入数据的 {@link ByteArrayOutputStream}。
   * @param parent 原始的父 {@link ServletOutputStream}，写入操作也将被委托给它。
   */
  public BufferedServletOutputStream(final ByteArrayOutputStream output,
      final ServletOutputStream parent) {
    this.output = output;
    this.parent = parent;
  }

  /**
   * 检查父 {@link ServletOutputStream} 是否已准备好接受数据。
   *
   * @return 如果父输出流已准备好，则返回 {@code true}；否则返回 {@code false}。
   */
  @Override
  public boolean isReady() {
    return parent.isReady();
  }

  /**
   * 为父 {@link ServletOutputStream} 设置写入监听器。
   *
   * @param writeListener 要设置的 {@link WriteListener}。
   */
  @Override
  public void setWriteListener(final WriteListener writeListener) {
    parent.setWriteListener(writeListener);
  }

  /**
   * 将指定的字节写入内部缓冲区和父 {@link ServletOutputStream}。
   *
   * @param b 要写入的字节。
   * @throws IOException 如果写入父输出流时发生I/O错误。
   */
  @Override
  public void write(final int b) throws IOException {
    parent.write(b);
    output.write(b);
  }

  /**
   * 将指定字节数组中的所有字节写入内部缓冲区和父 {@link ServletOutputStream}。
   *
   * @param b 要写入的字节数组。
   * @throws IOException 如果写入父输出流时发生I/O错误。
   */
  @Override
  public void write(final byte[] b) throws IOException {
    parent.write(b);
    output.write(b);
  }

  /**
   * 将指定字节数组中从偏移量 {@code off} 开始的 {@code len} 个字节写入内部缓冲区和父 {@link ServletOutputStream}。
   *
   * @param b 字节数组。
   * @param off 数组中开始写入的偏移量。
   * @param len 要写入的字节数。
   * @throws IOException 如果写入父输出流时发生I/O错误。
   */
  @Override
  public void write(final byte[] b, final int off, final int len)
      throws IOException {
    parent.write(b, off, len);
    output.write(b, off, len);
  }

  /**
   * 清空父 {@link ServletOutputStream} 的缓冲区。
   * 注意：此方法不会清空内部的 {@link ByteArrayOutputStream}，因为它通常在所有数据写入完成后才被读取。
   *
   * @throws IOException 如果清空父输出流时发生I/O错误。
   */
  @Override
  public void flush() throws IOException {
    parent.flush();
  }
}
