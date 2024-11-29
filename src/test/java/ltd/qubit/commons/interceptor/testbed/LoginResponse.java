////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor.testbed;

import java.io.Serializable;

import javax.annotation.Nullable;

import jakarta.validation.constraints.Size;

import ltd.qubit.commons.annotation.Identifier;
import ltd.qubit.commons.lang.Equality;
import ltd.qubit.commons.lang.Hash;
import ltd.qubit.commons.text.tostring.ToStringBuilder;
import ltd.qubit.model.commons.Token;
import ltd.qubit.model.person.Gender;
import ltd.qubit.model.person.UserInfo;
import ltd.qubit.model.system.Session;

/**
 * 此模型表示用户的登录信息。
 *
 * @author 胡海星
 */
public class LoginResponse implements Serializable {

  private static final long serialVersionUID = -6185309661495786348L;

  /**
   * 用户ID，唯一标识，系统自动生成。
   */
  @Identifier
  private Long id;

  /**
   * 用户名。
   */
  @Size(min = 1, max = 64)
  private String username;

  /**
   * 真实姓名。
   */
  @Size(min = 1, max = 128)
  private String name;

  /**
   * 昵称。
   */
  @Size(min = 1, max = 64)
  @Nullable
  private String nickname;

  /**
   * 性别。
   */
  @Nullable
  private Gender gender;

  /**
   * 头像。
   */
  @Size(min = 1, max = 512)
  @Nullable
  private String avatar;

  /**
   * 用户的访问令牌。
   */
  private Token token;

  /**
   * 用户的权限列表。
   */
  private String[] privileges;

  /**
   * 用户的角色列表。
   */
  private String[] roles;

  public LoginResponse() {
    // empty
  }

  public LoginResponse(final Session session) {
    final UserInfo user = session.getUser();
    id = user.getId();
    username = user.getUsername();
    name = user.getName();
    nickname = user.getNickname();
    gender = user.getGender();
    avatar = user.getAvatar();
    token = session.getToken();
    privileges = session.getPrivileges();
    roles = session.getRoles();
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@Nullable final String nickname) {
    this.nickname = nickname;
  }

  @Nullable
  public Gender getGender() {
    return gender;
  }

  public void setGender(@Nullable final Gender gender) {
    this.gender = gender;
  }

  @Nullable
  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(@Nullable final String avatar) {
    this.avatar = avatar;
  }

  public Token getToken() {
    return token;
  }

  public void setToken(final Token token) {
    this.token = token;
  }

  public String[] getPrivileges() {
    return privileges;
  }

  public void setPrivileges(final String[] privileges) {
    this.privileges = privileges;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(final String[] roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final LoginResponse other = (LoginResponse) o;
    return Equality.equals(id, other.id)
        && Equality.equals(username, other.username)
        && Equality.equals(name, other.name)
        && Equality.equals(nickname, other.nickname)
        && Equality.equals(gender, other.gender)
        && Equality.equals(avatar, other.avatar)
        && Equality.equals(token, other.token)
        && Equality.equals(privileges, other.privileges)
        && Equality.equals(roles, other.roles);
  }

  @Override
  public int hashCode() {
    final int multiplier = 7;
    int result = 3;
    result = Hash.combine(result, multiplier, id);
    result = Hash.combine(result, multiplier, username);
    result = Hash.combine(result, multiplier, name);
    result = Hash.combine(result, multiplier, nickname);
    result = Hash.combine(result, multiplier, gender);
    result = Hash.combine(result, multiplier, avatar);
    result = Hash.combine(result, multiplier, token);
    result = Hash.combine(result, multiplier, privileges);
    result = Hash.combine(result, multiplier, roles);
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("username", username)
        .append("name", name)
        .append("nickname", nickname)
        .append("gender", gender)
        .append("avatar", avatar)
        .append("token", token)
        .append("privileges", privileges)
        .append("roles", roles)
        .toString();
  }
}
