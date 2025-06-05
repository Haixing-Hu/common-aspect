////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ltd.qubit.commons.lang.StringUtils;

/**
 * 提供与 {@link HttpServletRequest} 和 {@link HttpServletResponse} 相关的实用方法。
 * 
 * @author 胡海星
 */
public class HttpServletUtils {

  /**
   * 判断HTTP请求是否为 multipart 类型。
   *
   * @param request HTTP请求对象。
   * @return 如果请求的 Content-Type 以 "multipart/" 开头（忽略大小写），则返回 {@code true}；否则返回 {@code false}。
   */
  public static boolean isMultipart(final HttpServletRequest request) {
    return StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/");
  }

  /**
   * 判断HTTP请求是否为 www-form 类型 (application/x-www-form-urlencoded)。
   *
   * @param request HTTP请求对象。
   * @return 如果请求的 Content-Type 以 "application/x-www-form-urlencoded" 开头（忽略大小写），则返回 {@code true}；否则返回 {@code false}。
   */
  public static boolean isWwwForm(final HttpServletRequest request) {
    return StringUtils.startsWithIgnoreCase(request.getContentType(), "application/x-www-form-urlencoded");
  }

  /**
   * 判定响应是否为文件下载。
   *
   * @param response {@link HttpServletResponse} 对象。
   * @return 若响应是文件下载，返回{@code true}；否则返回{@code false}。
   */
  public static boolean isFileDownload(final HttpServletResponse response) {
    // 获取 Content-Disposition 和 Content-Type
    final String contentDisposition = response.getHeader("Content-Disposition");
    // 判定 Content-Disposition 是否指示附件或包含文件名
    return (contentDisposition != null)
        && (contentDisposition.contains("attachment") || contentDisposition.contains("filename"));
  }

  /**
   * 判定响应是否为二进制数据。
   *
   * @param response {@link HttpServletResponse} 对象。
   * @return 若响应是二进制数据，返回{@code true}；否则返回{@code false}。
   */
  public static boolean isBinary(final HttpServletResponse response) {
    final String contentType = response.getContentType();
    // 判定 Content-Type 是否为常见的二进制数据类型
    if (contentType != null) {
      return isBinaryContentType(contentType);
    }
    return false;
  }

  /**
   * 判定请求是否为二进制数据。
   *
   * @param request {@link HttpServletRequest} 对象。
   * @return 若请求是二进制数据，返回{@code true}；否则返回{@code false}。
   */
  public static boolean isBinary(final HttpServletRequest request) {
    final String contentType = request.getContentType();
    // 判定 Content-Type 是否为常见的二进制数据类型
    if (contentType != null) {
      return isBinaryContentType(contentType);
    }
    return false;
  }

  /**
   * 判定响应是否为文本数据。
   *
   * @param response {@link HttpServletResponse} 对象。
   * @return 若响应是文本数据，返回{@code true}；否则返回{@code false}。
   */
  public static boolean isTextual(final HttpServletResponse response) {
    final String contentType = response.getContentType();
    // 判定 Content-Type 是否为常见的文本数据类型
    if (contentType != null) {
      return isTextualContentType(contentType);
    }
    return false;
  }

  /**
   * 判定请求是否为文本数据。
   *
   * @param request {@link HttpServletRequest} 对象。
   * @return 若请求是文本数据，返回{@code true}；否则返回{@code false}。
   */
  public static boolean isTextual(final HttpServletRequest request) {
    final String contentType = request.getContentType();
    // 判定 Content-Type 是否为常见的文本数据类型
    if (contentType != null) {
      return isTextualContentType(contentType);
    }
    return false;
  }

  /**
   * 判断 Content-Type 是否为常见的文本数据类型。
   *
   * @param contentType 响应的 Content-Type。
   * @return 若为文本数据类型，返回{@code true}；否则返回{@code false}。
   */
  private static boolean isTextualContentType(final String contentType) {
    return contentType.startsWith("text/")
        || TEXTUAL_APPLICATION_TYPES.contains(contentType);
  }

  /**
   * 判断 Content-Type 是否为常见的二进制数据类型。
   *
   * @param contentType 响应的 Content-Type。
   * @return 若为二进制数据类型，返回{@code true}；否则返回{@code false}。
   */
  private static boolean isBinaryContentType(final String contentType) {
    if (contentType.startsWith("application/")) {
      // 排除常见文本类型
      return !TEXTUAL_APPLICATION_TYPES.contains(contentType);
    }
    return contentType.startsWith("image/")                    // 图片
        || contentType.startsWith("audio/")                    // 音频
        || contentType.startsWith("video/")                    // 视频
        || contentType.startsWith("multipart/");               // multipart
  }

  /**
   * 存储常见的文本类型的 application/* Content-Type 集合。
   */
  // 定义需要排除的常见文本类型
  private static final Set<String> TEXTUAL_APPLICATION_TYPES = new HashSet<>();
  static {
    TEXTUAL_APPLICATION_TYPES.add("application/json");
    TEXTUAL_APPLICATION_TYPES.add("application/json;charset=UTF-8");
    TEXTUAL_APPLICATION_TYPES.add("application/graphql");
    TEXTUAL_APPLICATION_TYPES.add("application/graphql+json");
    TEXTUAL_APPLICATION_TYPES.add("application/graphql-response+json");
    TEXTUAL_APPLICATION_TYPES.add("application/x-www-form-urlencoded");
    TEXTUAL_APPLICATION_TYPES.add("application/problem+json");
    TEXTUAL_APPLICATION_TYPES.add("application/problem+json;charset=UTF-8");
    TEXTUAL_APPLICATION_TYPES.add("application/javascript");
    TEXTUAL_APPLICATION_TYPES.add("application/yaml");
    TEXTUAL_APPLICATION_TYPES.add("application/xml");
    TEXTUAL_APPLICATION_TYPES.add("application/xhtml+xml");
    TEXTUAL_APPLICATION_TYPES.add("application/atom+xml");
    TEXTUAL_APPLICATION_TYPES.add("application/rss+xml");
    TEXTUAL_APPLICATION_TYPES.add("application/problem+xml");
  }
}
