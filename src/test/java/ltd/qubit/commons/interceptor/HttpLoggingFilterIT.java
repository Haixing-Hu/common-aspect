////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.json.JsonMapper;

import ltd.qubit.commons.interceptor.testbed.LoginParams;
import ltd.qubit.commons.interceptor.testbed.LoginResponse;
import ltd.qubit.commons.text.jackson.CustomizedJsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ltd.qubit.commons.interceptor.testbed.HelloController.MOBILE;
import static ltd.qubit.commons.interceptor.testbed.HelloController.USERNAME;
import static ltd.qubit.commons.interceptor.testbed.HelloController.USER_ID;
import static ltd.qubit.commons.interceptor.testbed.HelloController.VERIFY_CODE;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:test-context.xml")
@WebAppConfiguration
public class HttpLoggingFilterIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .addFilter(new HttpLoggingFilter())
            .build();
    }

    @Test
    public void testHello() throws Exception {
        this.mockMvc.perform(get("/hello").param("name", "Bill Gates"))
            .andExpect(status().isOk());
    }

    @Test
    public void testLogin() throws Exception {
        final LoginParams params = new LoginParams();
        params.setMobile(MOBILE);
        params.setVerifyCode(VERIFY_CODE);
        final JsonMapper mapper = new CustomizedJsonMapper();
        final String response = this.mockMvc
            .perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(params)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        final LoginResponse expected = new LoginResponse();
        expected.setId(USER_ID);
        expected.setUsername(USERNAME);
        System.out.println("response = " + response);
        final LoginResponse actual = mapper.readValue(response, LoginResponse.class);
        assertEquals(expected, actual);
    }

    // TODO: 测试 content-type 为 www-form-url-encoded 和 multipart/form-data 的情况
}
