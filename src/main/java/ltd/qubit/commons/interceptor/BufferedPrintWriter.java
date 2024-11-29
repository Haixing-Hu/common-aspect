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

public class BufferedPrintWriter extends PrintWriter {

  private final PrintWriter parent;

  public BufferedPrintWriter(final OutputStream output,
      final PrintWriter parent) {
    super(output);
    this.parent = parent;
  }

  @Override
  public void flush() {
    parent.flush();
    super.flush();
  }

  @Override
  public void close() {
    parent.close();
    super.close();
  }

  @Override
  public boolean checkError() {
    return parent.checkError();
  }

  @Override
  public void write(final int c) {
    parent.write(c);
    super.write(c);
  }

  @Override
  public void write(final char[] buf, final int off, final int len) {
    parent.write(buf, off, len);
    super.write(buf, off, len);
  }

  @Override
  public void write(final char[] buf) {
    parent.write(buf);
    super.write(buf);
  }

  @Override
  public void write(final String s, final int off, final int len) {
    parent.write(s, off, len);
    super.write(s, off, len);
  }

  @Override
  public void write(final String s) {
    parent.write(s);
    super.write(s);
  }

  @Override
  public void print(final boolean b) {
    parent.print(b);
    super.print(b);
  }

  @Override
  public void print(final char c) {
    parent.print(c);
    super.print(c);
  }

  @Override
  public void print(final int i) {
    parent.print(i);
    super.print(i);
  }

  @Override
  public void print(final long l) {
    parent.print(l);
    super.print(l);
  }

  @Override
  public void print(final float f) {
    parent.print(f);
    super.print(f);
  }

  @Override
  public void print(final double d) {
    parent.print(d);
    super.print(d);
  }

  @Override
  public void print(final char[] s) {
    parent.print(s);
    super.print(s);
  }

  @Override
  public void print(final String s) {
    parent.print(s);
    super.print(s);
  }

  @Override
  public void print(final Object obj) {
    parent.print(obj);
    super.print(obj);
  }

  @Override
  public void println() {
    parent.println();
    super.println();
  }

  @Override
  public void println(final boolean x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final char x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final int x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final long x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final float x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final double x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final char[] x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final String x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public void println(final Object x) {
    parent.println(x);
    super.println(x);
  }

  @Override
  public PrintWriter printf(final String format, final Object... args) {
    parent.printf(format, args);
    return super.printf(format, args);
  }

  @Override
  public PrintWriter printf(final Locale l, final String format,
      final Object... args) {
    parent.printf(l, format, args);
    return super.printf(l, format, args);
  }

  @Override
  public PrintWriter format(final String format, final Object... args) {
    parent.format(format, args);
    return super.format(format, args);
  }

  @Override
  public PrintWriter format(final Locale l, final String format,
      final Object... args) {
    parent.format(l, format, args);
    return super.format(l, format, args);
  }

  @Override
  public PrintWriter append(final CharSequence csq) {
    parent.append(csq);
    return super.append(csq);
  }

  @Override
  public PrintWriter append(final CharSequence csq, final int start,
      final int end) {
    parent.append(csq, start, end);
    return super.append(csq, start, end);
  }

  @Override
  public PrintWriter append(final char c) {
    parent.append(c);
    return super.append(c);
  }
}
