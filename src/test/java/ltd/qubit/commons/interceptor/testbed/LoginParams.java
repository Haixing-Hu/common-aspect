////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor.testbed;

import java.io.Serial;
import java.io.Serializable;

import javax.annotation.Nullable;

import jakarta.validation.constraints.Size;

import ltd.qubit.commons.lang.Assignable;
import ltd.qubit.commons.lang.Assignment;
import ltd.qubit.commons.lang.Equality;
import ltd.qubit.commons.lang.Hash;
import ltd.qubit.commons.text.tostring.ToStringBuilder;
import ltd.qubit.model.contact.Phone;
import ltd.qubit.model.person.SocialNetwork;
import ltd.qubit.model.system.Environment;
import ltd.qubit.model.util.Desensitizable;

import static ltd.qubit.commons.lang.Argument.requireNonNull;

/**
 * 此数据结构封装了登录操作所需提供的参数。
 *
 * <p>注意请求参数中，{@code username}, {@code email}, {@code mobile},
 * 或{@code openId}至少需要有一个。
 *
 * @author 胡海星
 */
public class LoginParams implements Serializable, Desensitizable<LoginParams>,
    Assignable<LoginParams> {

  @Serial
  private static final long serialVersionUID = 332107242712373191L;

  /**
   * 登录使用的用户名。若使用用户名登录，则此项不能为{@code null}，否则可以为
   * {@code null}。
   */
  @Size(min = 1, max = 64)
  @Nullable
  private String username;

  /**
   * 登录使用的电子邮件地址。若使用电子邮件地址登录，则此项不能为{@code null}，否则可以为
   * {@code null}。
   */
  @Size(min = 1, max = 512)
  @Nullable
  private String email;

  /**
   * 登录使用的手机号码，若使用手机号码登录，则此项不能为{@code null}，否则可以为
   * {@code null}。
   */
  @Nullable
  private Phone mobile;

  /**
   * 登录使用的密码。若使用用户名、手机号码或电子邮件加密码登录，则此项不能为{@code null}，否
   * 则可以为{@code null}。
   */
  @Size(min = 1, max = 64)
  @Nullable
  private String password;

  /**
   * 登录使用的验证码。若使用手机号码或电子邮件加验证码登录，则此项不能为{@code null}，否则
   * 可以为{@code null}。
   */
  @Size(min = 1, max = 64)
  @Nullable
  private String verifyCode;

  /**
   * 登录使用的Open ID所属的社交网络，若使用Open ID登录，则此项不能为{@code null}，否则可
   * 以为{@code null}。
   */
  @Nullable
  private SocialNetwork socialNetwork;

  /**
   * 登录使用的Open ID所属的社交网络下的APP ID，若使用Open ID登录，则此项不能为
   * {@code null}，否则可以为{@code null}。
   */
  @Size(min = 1, max = 64)
  @Nullable
  private String appId;

  /**
   * 登录使用的Open ID。若使用Open ID登录，则此项不能为{@code null}，否则可以为
   * {@code null}。
   */
  @Size(min = 1, max = 64)
  @Nullable
  private String openId;

  /**
   * 登录时的客户端环境，可以为{@code null}。
   */
  @Nullable
  private Environment environment;

  public LoginParams() {
    // empty
  }

  public LoginParams(final LoginParams other) {
    assign(other);
  }

  @Override
  public void assign(final LoginParams other) {
    requireNonNull("other", other);
    username = other.username;
    email = other.email;
    mobile = Assignment.clone(other.mobile);
    password = other.password;
    verifyCode = other.verifyCode;
    socialNetwork = other.socialNetwork;
    appId = other.appId;
    openId = other.openId;
    environment = Assignment.clone(other.environment);
  }

  @Override
  public LoginParams cloneEx() {
    return new LoginParams(this);
  }

  @Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(@Nullable final String username) {
    this.username = username;
  }

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable final String email) {
    this.email = email;
  }

  @Nullable
  public Phone getMobile() {
    return mobile;
  }

  public void setMobile(@Nullable final Phone mobile) {
    this.mobile = mobile;
  }

  @Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nullable final String password) {
    this.password = password;
  }

  @Nullable
  public String getVerifyCode() {
    return verifyCode;
  }

  public void setVerifyCode(@Nullable final String verifyCode) {
    this.verifyCode = verifyCode;
  }

  @Nullable
  public SocialNetwork getSocialNetwork() {
    return socialNetwork;
  }

  public void setSocialNetwork(@Nullable final SocialNetwork socialNetwork) {
    this.socialNetwork = socialNetwork;
  }

  @Nullable
  public String getAppId() {
    return appId;
  }

  public void setAppId(@Nullable final String appId) {
    this.appId = appId;
  }

  @Nullable
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(@Nullable final String openId) {
    this.openId = openId;
  }

  @Nullable
  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(@Nullable final Environment environment) {
    this.environment = environment;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final LoginParams other = (LoginParams) o;
    return Equality.equals(username, other.username)
        && Equality.equals(email, other.email)
        && Equality.equals(mobile, other.mobile)
        && Equality.equals(password, other.password)
        && Equality.equals(verifyCode, other.verifyCode)
        && Equality.equals(socialNetwork, other.socialNetwork)
        && Equality.equals(appId, other.appId)
        && Equality.equals(openId, other.openId)
        && Equality.equals(environment, other.environment);
  }

  @Override
  public int hashCode() {
    final int multiplier = 7;
    int result = 3;
    result = Hash.combine(result, multiplier, username);
    result = Hash.combine(result, multiplier, email);
    result = Hash.combine(result, multiplier, mobile);
    result = Hash.combine(result, multiplier, password);
    result = Hash.combine(result, multiplier, verifyCode);
    result = Hash.combine(result, multiplier, socialNetwork);
    result = Hash.combine(result, multiplier, appId);
    result = Hash.combine(result, multiplier, openId);
    result = Hash.combine(result, multiplier, environment);
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("username", username)
        .append("email", email)
        .append("mobile", mobile)
        .append("password", password)
        .append("verifyCode", verifyCode)
        .append("socialNetwork", socialNetwork)
        .append("appId", appId)
        .append("openId", openId)
        .append("environment", environment)
        .toString();
  }

  @Override
  public void desensitize() {
    if (password != null) {
      password = "******** (desensitized)";
    }
    if (openId != null) {
      openId = "******** (desensitized)";
    }
  }
}
