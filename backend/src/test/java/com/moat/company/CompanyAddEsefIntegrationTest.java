package com.moat.company;

import com.moat.report.FinancialReportRepository;
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

class CompanyAddEsefIntegrationTest extends AbstractIntegrationTest {

    @Autowired CompanyRepository companyRepository;
    @Autowired FinancialReportRepository reportRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void clean() {
        reportRepository.deleteAll();
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

    private String createCompanyFromEsef(Cookie cookie) throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", samplePackage());
        MvcResult result = mockMvc.perform(multipart("/companies/esef").file(file).cookie(cookie))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void addEsefYear_matchingCompany_returns200() throws Exception {
        Cookie cookie = login("alice@example.com");

        MvcResult created = mockMvc.perform(post("/companies").cookie(cookie)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Rainbow Tours Spółka Akcyjna\",\"financials\":{"
                                + "\"fiscalYear\":2024,\"currency\":\"PLN\",\"revenue\":1000}}"))
                .andExpect(status().isCreated()).andReturn();
        String companyId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", samplePackage());

        mockMvc.perform(multipart("/companies/" + companyId + "/financials/esef")
                        .file(file).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fiscalYear").value(2025))
                .andExpect(jsonPath("$.revenue").exists());
    }

    @Test
    void addEsefYear_duplicateYear_returns409() throws Exception {
        Cookie cookie = login("alice@example.com");
        String companyId = createCompanyFromEsef(cookie);

        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", samplePackage());

        mockMvc.perform(multipart("/companies/" + companyId + "/financials/esef")
                        .file(file).cookie(cookie))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("2025")));
    }

    @Test
    void addEsefYear_mismatchedCompany_returns422() throws Exception {
        Cookie cookie = login("alice@example.com");

        MvcResult created = mockMvc.perform(post("/companies").cookie(cookie)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Acme Corp\",\"financials\":{\"fiscalYear\":2024,\"currency\":\"PLN\",\"revenue\":1000}}"))
                .andExpect(status().isCreated()).andReturn();
        String companyId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", samplePackage());

        mockMvc.perform(multipart("/companies/" + companyId + "/financials/esef")
                        .file(file).cookie(cookie))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("Acme Corp")));
    }

    @Test
    void addEsefYear_invalidFile_returns422() throws Exception {
        Cookie cookie = login("alice@example.com");
        String companyId = createCompanyFromEsef(cookie);

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.xbri", "application/octet-stream", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/companies/" + companyId + "/financials/esef")
                        .file(file).cookie(cookie))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void addEsefYear_withoutAuth_returns401() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/companies/" + java.util.UUID.randomUUID() + "/financials/esef")
                        .file(file))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addEsefYear_nonExistentCompany_returns404() throws Exception {
        Cookie cookie = login("alice@example.com");

        MockMultipartFile file = new MockMultipartFile(
                "file", "rainbow-tours.xbri", "application/octet-stream", samplePackage());

        mockMvc.perform(multipart("/companies/" + java.util.UUID.randomUUID() + "/financials/esef")
                        .file(file).cookie(cookie))
                .andExpect(status().isNotFound());
    }
}
