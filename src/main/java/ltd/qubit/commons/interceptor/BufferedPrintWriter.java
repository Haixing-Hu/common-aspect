////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * 一个 {@link PrintWriter} 的子类，它将其所有操作委托给父类（即写入提供的 {@link OutputStream}）
 * 以及一个额外的"父" {@link PrintWriter} 实例。
 * <p>
 * 这允许将输出同时写入例如内存缓冲区和原始的响应写入器。
 * 
 * @author 胡海星
 */
public class BufferedPrintWriter extends PrintWriter {

  /**
   * 另一个 PrintWriter 实例，所有写操作也会被委托给它。
   */
  private final PrintWriter parent;

  /**
   *构造一个 {@code BufferedPrintWriter}。
   *
   * @param output 
   *    一个 {@link OutputStream}，作为此 {@code PrintWriter} 的主要输出目标（例如，一个内部缓冲区）。
   *    所有通过 {@code super} 调用的写操作将写入此流。
   * @param parent 
   *    另一个 {@link PrintWriter} 实例，所有写操作也将被委托给它。
   */
  public BufferedPrintWriter(final OutputStream output,
      final PrintWriter parent) {
    super(output);
    this.parent = parent;
  }

  /**
   * 清空此写入器和父写入器的缓冲区。
   */
  @Override
  public void flush() {
    parent.flush();
    super.flush();
  }

  /**
   * 关闭此写入器和父写入器。
   */
  @Override
  public void close() {
    parent.close();
    super.close();
  }

  /**
   * 检查父写入器是否发生错误。
   *
   * @return 如果父写入器遇到错误，则返回true。
   */
  @Override
  public boolean checkError() {
    return parent.checkError();
  }

  /**
   * 将单个字符写入此写入器和父写入器。
   *
   * @param c 要写入的字符的int值。
   */
  @Override
  public void write(final int c) {
    parent.write(c);
    super.write(c);
  }

  /**
   * 将字符数组的一部分写入此写入器和父写入器。
   *
   * @param buf 字符数组。
   * @param off 起始偏移量。
   * @param len 要写入的字符数。
   */
  @Override
  public void write(final char[] buf, final int off, final int len) {
    parent.write(buf, off, len);
    super.write(buf, off, len);
  }

  /**
   * 将整个字符数组写入此写入器和父写入器。
   *
   * @param buf 字符数组。
   */
  @Override
  public void write(final char[] buf) {
    parent.write(buf);
    super.write(buf);
  }

  /**
   * 将字符串的一部分写入此写入器和父写入器。
   *
   * @param s 字符串。
   * @param off 起始偏移量。
   * @param len 要写入的字符数。
   */
  @Override
  public void write(final String s, final int off, final int len) {
    parent.write(s, off, len);
    super.write(s, off, len);
  }

  /**
   * 将整个字符串写入此写入器和父写入器。
   *
   * @param s 字符串。
   */
  @Override
  public void write(final String s) {
    parent.write(s);
    super.write(s);
  }

  /**
   * 将布尔值打印到此写入器和父写入器。
   *
   * @param b 要打印的布尔值。
   */
  @Override
  public void print(final boolean b) {
    parent.print(b);
    super.print(b);
  }

  /**
   * 将字符打印到此写入器和父写入器。
   *
   * @param c 要打印的字符。
   */
  @Override
  public void print(final char c) {
    parent.print(c);
    super.print(c);
  }

  /**
   * 将整数打印到此写入器和父写入器。
   *
   * @param i 要打印的整数。
   */
  @Override
  public void print(final int i) {
    parent.print(i);
    super.print(i);
  }

  /**
   * 将长整数打印到此写入器和父写入器。
   *
   * @param l 要打印的长整数。
   */
  @Override
  public void print(final long l) {
    parent.print(l);
    super.print(l);
  }

  /**
   * 将浮点数打印到此写入器和父写入器。
   *
   * @param f 要打印的浮点数。
   */
  @Override
  public void print(final float f) {
    parent.print(f);
    super.print(f);
  }

  /**
   * 将双精度浮点数打印到此写入器和父写入器。
   *
   * @param d 要打印的双精度浮点数。
   */
  @Override
  public void print(final double d) {
    parent.print(d);
    super.print(d);
  }

  /**
   * 将字符数组打印到此写入器和父写入器。
   *
   * @param s 要打印的字符数组。
   */
  @Override
  public void print(final char[] s) {
    parent.print(s);
    super.print(s);
  }

  /**
   * 将字符串打印到此写入器和父写入器。
   *
   * @param s 要打印的字符串。
   */
  @Override
  public void print(final String s) {
    parent.print(s);
    super.print(s);
  }

  /**
   * 将对象打印到此写入器和父写入器。
   *
   * @param obj 要打印的对象。
   */
  @Override
  public void print(final Object obj) {
    parent.print(obj);
    super.print(obj);
  }

  /**
   * 在此写入器和父写入器中打印一个换行符。
   */
  @Override
  public void println() {
    parent.println();
    super.println();
  }

  /**
   * 将布尔值和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的布尔值。
   */
  @Override
  public void println(final boolean x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将字符和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的字符。
   */
  @Override
  public void println(final char x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将整数和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的整数。
   */
  @Override
  public void println(final int x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将长整数和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的长整数。
   */
  @Override
  public void println(final long x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将浮点数和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的浮点数。
   */
  @Override
  public void println(final float x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将双精度浮点数和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的双精度浮点数。
   */
  @Override
  public void println(final double x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将字符数组和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的字符数组。
   */
  @Override
  public void println(final char[] x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将字符串和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的字符串。
   */
  @Override
  public void println(final String x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 将对象和换行符打印到此写入器和父写入器。
   *
   * @param x 要打印的对象。
   */
  @Override
  public void println(final Object x) {
    parent.println(x);
    super.println(x);
  }

  /**
   * 使用指定的格式字符串和参数，在此写入器和父写入器中打印格式化的字符串。
   *
   * @param format 格式字符串。
   * @param args 参数。
   * @return 此写入器。
   */
  @Override
  public PrintWriter printf(final String format, final Object... args) {
    parent.printf(format, args);
    return super.printf(format, args);
  }

  /**
   * 使用指定的区域设置、格式字符串和参数，在此写入器和父写入器中打印格式化的字符串。
   *
   * @param l 区域设置。
   * @param format 格式字符串。
   * @param args 参数。
   * @return 此写入器。
   */
  @Override
  public PrintWriter printf(final Locale l, final String format,
      final Object... args) {
    parent.printf(l, format, args);
    return super.printf(l, format, args);
  }

  /**
   * 使用指定的格式字符串和参数，在此写入器和父写入器中写入格式化的字符串。
   * 这是 {@link #printf(String, Object...)} 的便捷方法。
   *
   * @param format 格式字符串。
   * @param args 参数。
   * @return 此写入器。
   */
  @Override
  public PrintWriter format(final String format, final Object... args) {
    parent.format(format, args);
    return super.format(format, args);
  }

  /**
   * 使用指定的区域设置、格式字符串和参数，在此写入器和父写入器中写入格式化的字符串。
   * 这是 {@link #printf(Locale, String, Object...)} 的便捷方法。
   *
   * @param l 区域设置。
   * @param format 格式字符串。
   * @param args 参数。
   * @return 此写入器。
   */
  @Override
  public PrintWriter format(final Locale l, final String format,
      final Object... args) {
    parent.format(l, format, args);
    return super.format(l, format, args);
  }

  /**
   * 将指定的字符序列追加到此写入器和父写入器。
   *
   * @param csq 要追加的字符序列。
   * @return 此写入器。
   */
  @Override
  public PrintWriter append(final CharSequence csq) {
    parent.append(csq);
    return super.append(csq);
  }

  /**
   * 将指定字符序列的子序列追加到此写入器和父写入器。
   *
   * @param csq 要追加的字符序列。
   * @param start 子序列的起始索引。
   * @param end 子序列的结束索引。
   * @return 此写入器。
   */
  @Override
  public PrintWriter append(final CharSequence csq, final int start,
      final int end) {
    parent.append(csq, start, end);
    return super.append(csq, start, end);
  }

  /**
   * 将指定的字符追加到此写入器和父写入器。
   *
   * @param c 要追加的字符。
   * @return 此写入器。
   */
  @Override
  public PrintWriter append(final char c) {
    parent.append(c);
    return super.append(c);
  }
}
