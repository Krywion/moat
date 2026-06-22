package com.moat.company;

import com.moat.support.AbstractIntegrationTest;
import com.moat.user.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyRefreshMarketIntegrationTest extends AbstractIntegrationTest {

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

    private String createCompany(Cookie cookie) throws Exception {
        MvcResult res = mockMvc.perform(post("/companies").cookie(cookie).contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Acme\",\"ticker\":\"RBW\",\"financials\":{"
                                + "\"fiscalYear\":2024,\"currency\":\"PLN\",\"revenue\":1000,\"ebit\":200,"
                                + "\"depreciation\":50,\"netProfit\":100,\"totalDebt\":300,\"equity\":500,"
                                + "\"operatingCashFlow\":120}}"))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void refreshMarket_returns200() throws Exception {
        // app.market.enabled=false w testach → pola rynkowe null, ale endpoint zwraca 200
        Cookie cookie = login("alice@example.com");
        String id = createCompany(cookie);

        mockMvc.perform(post("/companies/" + id + "/refresh-market").cookie(cookie))
                .andExpect(status().isOk());
    }

    @Test
    void refreshMarket_ofAnotherUser_returns404() throws Exception {
        Cookie alice = login("alice@example.com");
        Cookie bob = login("bob@example.com");
        String id = createCompany(alice);

        mockMvc.perform(post("/companies/" + id + "/refresh-market").cookie(bob))
                .andExpect(status().isNotFound());
    }

    @Test
    void refreshMarket_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/companies/" + java.util.UUID.randomUUID() + "/refresh-market"))
                .andExpect(status().isUnauthorized());
    }
}
