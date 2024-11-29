////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor.testbed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ltd.qubit.commons.lang.Equality;
import ltd.qubit.model.contact.Phone;
import ltd.qubit.model.controller.LoginParams;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class HelloController {
  public static final Long USER_ID = 1001L;

  public static final String USERNAME = "bill";

  public static final String PASSWORD = "hello-world";

  public static final Phone MOBILE = new Phone("13602541234");

  public static final String VERIFY_CODE = "123456";

  @RequestMapping(path = "/hello",
      method = GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public String sayHello(@RequestParam final String name) {
    return "Hello " + name;
  }

  @RequestMapping(path = "/login",
      method = POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public LoginResponse login(@RequestBody final LoginParams params) {
    if (params.getUsername() != null) {
      return loginByUsernamePassword(params.getUsername(), params.getPassword());
    } else if (params.getMobile() != null) {
      return loginByMobileVerifyCode(params.getMobile(), params.getVerifyCode());
    } else {
      throw new IllegalArgumentException("Invalid params");
    }
  }

  private LoginResponse loginByUsernamePassword(final String username, final String password) {
    if (Equality.equals(username, USERNAME) && Equality.equals(password, PASSWORD)) {
      final LoginResponse response = new LoginResponse();
      response.setUsername(username);
      response.setId(USER_ID);
      return response;
    } else {
      throw new IllegalArgumentException("Invalid username, password");
    }
  }

  private LoginResponse loginByMobileVerifyCode(final Phone mobile, final String verifyCode) {
    if (Equality.equals(mobile, MOBILE) && Equality.equals(verifyCode, VERIFY_CODE)) {
      final LoginResponse response = new LoginResponse();
      response.setUsername(USERNAME);
      response.setId(USER_ID);
      return response;
    } else {
      throw new IllegalArgumentException("Invalid mobile, verifyCode");
    }
  }
}
