////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import jakarta.servlet.http.HttpServletRequest;

import ltd.qubit.commons.lang.StringUtils;

public class HttpServletUtils {

  public static boolean isMultipart(final HttpServletRequest request) {
    return StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/");
  }

  public static boolean isWwwForm(final HttpServletRequest request) {
    return StringUtils.startsWithIgnoreCase(request.getContentType(), "application/x-www-form-urlencoded");
  }
}
