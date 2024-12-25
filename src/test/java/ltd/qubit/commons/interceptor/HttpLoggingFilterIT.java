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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ltd.qubit.commons.interceptor.testbed.HelloController.MOBILE;
import static ltd.qubit.commons.interceptor.testbed.HelloController.PASSWORD;
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

    @Test
    public void loginWithInvalidContentTypeReturnsUnsupportedMediaType() throws Exception {
        final LoginParams params = new LoginParams();
        params.setMobile(MOBILE);
        params.setVerifyCode(VERIFY_CODE);
        final JsonMapper mapper = new CustomizedJsonMapper();
        this.mockMvc
            .perform(post("/login")
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content(mapper.writeValueAsBytes(params)))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void helloEndpointWithNoNameReturnsBadRequest() throws Exception {
        this.mockMvc.perform(get("/hello"))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void loginWithWwwFormUrlEncodedContentTypeReturnsOk() throws Exception {
        this.mockMvc
            .perform(post("/login-with-form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("username", USERNAME)
                .param("password", PASSWORD))
            .andExpect(status().isOk());
    }

    @Test
    public void uploadFileReturnsOriginalFilename() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain",
            "Test content".getBytes());
        this.mockMvc.perform(multipart("/upload")
                .file(file)
                .accept(MediaType.TEXT_PLAIN_VALUE)
            ).andExpect(status().isOk())
             .andExpect(content().string("Uploaded file: testfile.txt"));
    }

    @Test
    public void downloadFileReturnsBinaryData() throws Exception {
        this.mockMvc.perform(get("/download"))
                    .andExpect(status().isOk())
                    .andExpect(header()
                        .string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mockfile.bin\""))
                    .andExpect(content()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                    .andExpect(content()
                        .bytes("This is some binary data".getBytes()));
    }
}
