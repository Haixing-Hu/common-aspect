////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2022.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.common.interceptor;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import ltd.qubit.commons.text.CaseFormat;
import ltd.qubit.commons.text.jackson.CustomizedJsonMapper;

import static ltd.qubit.commons.lang.Argument.requireNonNull;

/**
 * 对请求参数的名称进行转换的过滤器。
 *
 * <p>此过滤器根据指定的名称转换策略，对请求参数的名称进行转换。例如，可以把所有请求参数从
 * {@link CaseFormat#LOWER_CAMEL} 转换为 {@link CaseFormat#LOWER_UNDERSCORE}</p>
 *
 * @author 胡海星
 */
public class RequestParameterNameConversionFilter extends OncePerRequestFilter {

  /**
   * 参数名称转换策略。
   */
  private CaseFormat namingStrategy = CustomizedJsonMapper.DEFAULT_NAMING_STRATEGY;

  /**
   * 是否允许使用未经转换的原始参数名称。
   */
  private boolean allowNonConvertedName = true;

  public final CaseFormat getNamingStrategy() {
    return namingStrategy;
  }

  public void setNamingStrategy(final CaseFormat namingStrategy) {
    this.namingStrategy = requireNonNull("namingStrategy", namingStrategy);
  }

  public final boolean isAllowNonConvertedName() {
    return allowNonConvertedName;
  }

  public void setAllowNonConvertedName(final boolean allowNonConvertedName) {
    this.allowNonConvertedName = allowNonConvertedName;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain filterChain)
      throws ServletException, IOException {
    final Map<String, String[]> parameters = new ConcurrentHashMap<>();
    for (final String name : request.getParameterMap().keySet()) {
      final String[] value = request.getParameterValues(name);
      final String convertedName = namingStrategy.to(CaseFormat.LOWER_CAMEL, name);
      parameters.put(convertedName, value);
      if (allowNonConvertedName) {
        parameters.put(name, value);
      }
      if (logger.isTraceEnabled()) {
        logger.trace("Convert parameter name '" + name + "' to '" + convertedName + "'");
      }
    }
    filterChain.doFilter(new HttpServletRequestWrapper(request) {
      @Override
      public String getParameter(final String name) {
        if (parameters.containsKey(name)) {
          final String[] values = parameters.get(name);
          return ((values != null && values.length > 0) ? values[0] : null);
        } else {
          return null;
        }
      }

      @Override
      public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
      }

      @Override
      public String[] getParameterValues(final String name) {
        return parameters.get(name);
      }

      @Override
      public Map<String, String[]> getParameterMap() {
        return parameters;
      }
    }, response);
  }
}
