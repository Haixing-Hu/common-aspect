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

  /**
   * 日志记录器。
   */
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

  /**
   * 检查此过滤器是否启用。
   *
   * @return 如果过滤器启用，则返回true；否则返回false。
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * 设置此过滤器的启用状态。
   *
   * @param enabled 如果为true，则启用过滤器；如果为false，则禁用过滤器。
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * 获取当前的参数名称转换策略。
   *
   * @return 当前使用的 {@link CaseFormat} 转换策略。
   */
  public final CaseFormat getNamingStrategy() {
    return namingStrategy;
  }

  /**
   * 设置参数名称的转换策略。
   *
   * @param namingStrategy 要使用的 {@link CaseFormat} 转换策略，不能为null。
   */
  public void setNamingStrategy(final CaseFormat namingStrategy) {
    this.namingStrategy = requireNonNull("namingStrategy", namingStrategy);
  }

  /**
   * 检查是否允许通过未经转换的原始名称访问参数。
   *
   * @return 如果允许使用原始名称，则返回true；否则返回false。
   */
  public final boolean isAllowNonConvertedName() {
    return allowNonConvertedName;
  }

  /**
   * 设置是否允许通过未经转换的原始名称访问参数。
   *
   * @param allowNonConvertedName 如果为true，则同时保留原始参数名和转换后的参数名；
   *                              如果为false，则只保留转换后的参数名。
   */
  public void setAllowNonConvertedName(final boolean allowNonConvertedName) {
    this.allowNonConvertedName = allowNonConvertedName;
  }

  /**
   * 初始化过滤器Bean。
   * <p>
   * 此方法从过滤器配置中读取 `enabled`、`namingStrategy` 和 `allowNonConvertedName` 初始化参数，
   * 并相应地配置此过滤器实例。
   * </p>
   */
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

  /**
   * 对HTTP请求的参数名称进行转换的核心过滤方法。
   * <p>
   * 如果过滤器已启用，则调用 {@link #doFilterInternalImpl} 执行实际的参数名称转换逻辑。
   * 如果过滤器被禁用，则直接将请求和响应传递给下一个过滤器，不进行转换。
   * </p>
   *
   * @param request 当前的HTTP请求。
   * @param response 当前的HTTP响应。
   * @param filterChain 过滤器链。
   * @throws ServletException 如果在处理请求或响应时发生Servlet相关异常。
   * @throws IOException 如果在处理请求或响应时发生I/O异常。
   */
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

  /**
   * 实际执行参数名称转换的内部方法。
   * <p>
   * 此方法遍历原始请求的所有参数，使用配置的 {@link #namingStrategy} 将参数名称转换为
   * {@link CaseFormat#LOWER_CAMEL} 格式。转换后的参数（以及根据 {@link #allowNonConvertedName}
   * 的设置可能包含的原始参数）会被放入一个新的Map中。
   * 然后，它创建一个匿名的 {@link HttpServletRequestWrapper} 子类实例，该实例使用这个新的参数Map，
   * 并将包装后的请求传递给过滤器链的下一个环节。
   * </p>
   *
   * @param request 当前的HTTP请求。
   * @param response 当前的HTTP响应。
   * @param filterChain 过滤器链。
   * @throws ServletException 如果在处理包装请求或传递到链中时发生Servlet相关异常。
   * @throws IOException 如果在处理包装请求或传递到链中时发生I/O异常。
   */
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
    // 创建一个 HttpServletRequestWrapper 的匿名子类，用于提供转换后的参数。
    filterChain.doFilter(new HttpServletRequestWrapper(request) {
      /**
       * 根据名称获取请求参数的第一个值。
       * <p>
       * 此方法会首先在转换后的参数映射中查找指定的名称。
       * </p>
       *
       * @param name 参数的名称。
       * @return 参数的第一个值；如果参数不存在或没有值，则返回null。
       */
      @Override
      public String getParameter(final String name) {
        if (parameters.containsKey(name)) {
          final String[] values = parameters.get(name);
          return ((values != null && values.length > 0) ? values[0] : null);
        } else {
          return null;
        }
      }

      /**
       * 获取所有请求参数名称的枚举。
       * <p>
       * 返回的名称来自于包含转换后（以及可能包含的原始）参数名称的映射。
       * </p>
       *
       * @return 一个包含所有参数名称的 {@link Enumeration}。
       */
      @Override
      public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
      }

      /**
       * 根据名称获取请求参数的所有值。
       * <p>
       * 此方法会从转换后的参数映射中获取指定名称的参数值数组。
       * </p>
       *
       * @param name 参数的名称。
       * @return 包含参数所有值的字符串数组；如果参数不存在，则返回null。
       */
      @Override
      public String[] getParameterValues(final String name) {
        return parameters.get(name);
      }

      /**
       * 获取包含所有请求参数的映射。
       * <p>
       * 返回的映射包含转换后（以及可能包含的原始）的参数名称及其对应的值数组。
       * </p>
       *
       * @return 一个从参数名称到参数值数组的 {@link Map}。
       */
      @Override
      public Map<String, String[]> getParameterMap() {
        return parameters;
      }
    }, response);
  }
}
