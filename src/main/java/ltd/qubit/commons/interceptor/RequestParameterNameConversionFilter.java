////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 是否启用此拦截器。
   */
  private boolean enabled = true;

  /**
   * 参数名称转换策略。
   */
  private CaseFormat namingStrategy = CustomizedJsonMapper.DEFAULT_NAMING_STRATEGY;

  /**
   * 是否允许使用未经转换的原始参数名称。
   */
  private boolean allowNonConvertedName = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

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
  protected void initFilterBean() {
    final FilterConfig filterConfig = getFilterConfig();
    if (filterConfig == null) {
      return;
    }
    final String enabledValue = filterConfig.getInitParameter("enabled");
    if (enabledValue != null) {
      enabled = "true".equals(enabledValue);
    }
    final String namingStrategyValue = filterConfig.getInitParameter("namingStrategy");
    if (namingStrategyValue != null) {
      namingStrategy = CaseFormat.valueOf(namingStrategyValue);
    }
    final String allowNonConvertedNameValue = filterConfig.getInitParameter("allowNonConvertedName");
    if (allowNonConvertedNameValue != null) {
      allowNonConvertedName = "true".equals(allowNonConvertedNameValue);
    }
  }

  @Override
  protected void doFilterInternal(@Nonnull final HttpServletRequest request,
      @Nonnull final HttpServletResponse response,
      @Nonnull final FilterChain filterChain) throws ServletException, IOException {
    if (enabled) {
      doFilterInternalImpl(request, response, filterChain);
    } else {
      logger.debug("RequestParameterNameConversionFilter is disabled, bypassing it.");
      filterChain.doFilter(request, response);
    }
  }

  private void doFilterInternalImpl(final HttpServletRequest request,
      @Nonnull final HttpServletResponse response,
      @Nonnull final FilterChain filterChain) throws ServletException, IOException {
    final Map<String, String[]> parameters = new ConcurrentHashMap<>();
    for (final String name : request.getParameterMap().keySet()) {
      final String[] value = request.getParameterValues(name);
      final String convertedName = namingStrategy.to(CaseFormat.LOWER_CAMEL, name);
      parameters.put(convertedName, value);
      if (allowNonConvertedName) {
        parameters.put(name, value);
      }
      logger.debug("Convert parameter name '{}' to '{}'", name, convertedName);
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
