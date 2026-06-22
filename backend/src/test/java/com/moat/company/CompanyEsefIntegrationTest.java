package com.moat.company;

import com.moat.support.AbstractIntegrationTest;
import com.moat.user.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyEsefIntegrationTest extends AbstractIntegrationTest {

    @Autowired CompanyRepository companyRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void clean() {
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Cookie login(String email) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
        MvcResult res = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn();
        return res.getResponse().getCookie("auth_token");
    }

    private byte[] samplePackage() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/esef/rainbow-tours.xbri")) {
            return in.readAllBytes();
        }
    }

    @Test
    void uploadEsef_createsCompanyWithAnalysis() throws Exception {
        Cookie cookie = login("alice@example.com");
        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", samplePackage());

        mockMvc.perform(multipart("/companies/esef").file(file).param("ticker", "RBW").cookie(cookie))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.reports[0].revenue").exists());
    }

    @Test
    void uploadInvalidFile_returns422() throws Exception {
        Cookie cookie = login("alice@example.com");
        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.xbri", "application/octet-stream", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/companies/esef").file(file).cookie(cookie))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void uploadWithoutAuth_returns401() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/companies/esef").file(file))
                .andExpect(status().isUnauthorized());
    }
}
