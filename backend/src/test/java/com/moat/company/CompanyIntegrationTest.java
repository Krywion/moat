package com.moat.company;

import com.moat.support.AbstractIntegrationTest;
import com.moat.user.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyIntegrationTest extends AbstractIntegrationTest {

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

    private String companyBody(int year, long revenue, long ebit, long netProfit) {
        return "{\"name\":\"Acme\",\"ticker\":\"ACM\",\"financials\":{"
                + "\"fiscalYear\":" + year + ",\"currency\":\"PLN\","
                + "\"revenue\":" + revenue + ",\"ebit\":" + ebit + ",\"depreciation\":50,"
                + "\"netProfit\":" + netProfit + ",\"totalDebt\":300,\"equity\":500,"
                + "\"operatingCashFlow\":120}}";
    }

    @Test
    void createCompany_computesIndicators() throws Exception {
        Cookie cookie = login("alice@example.com");

        mockMvc.perform(post("/companies").cookie(cookie).contentType(APPLICATION_JSON)
                        .content(companyBody(2024, 1000, 200, 100)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Acme"))
                .andExpect(jsonPath("$.reports[0].netMargin").value(0.1000))
                .andExpect(jsonPath("$.reports[0].ebitda").value(250));
    }

    @Test
    void list_isScopedToOwner() throws Exception {
        Cookie alice = login("alice@example.com");
        Cookie bob = login("bob@example.com");
        mockMvc.perform(post("/companies").cookie(alice).contentType(APPLICATION_JSON)
                .content(companyBody(2024, 1000, 200, 100))).andExpect(status().isCreated());

        mockMvc.perform(get("/companies").cookie(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void putFinancials_secondYear_computesYoyGrowth() throws Exception {
        Cookie cookie = login("alice@example.com");
        MvcResult created = mockMvc.perform(post("/companies").cookie(cookie)
                        .contentType(APPLICATION_JSON).content(companyBody(2023, 1000, 200, 100)))
                .andExpect(status().isCreated()).andReturn();
        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/companies/" + id + "/financials").cookie(cookie)
                        .contentType(APPLICATION_JSON)
                        .content("{\"fiscalYear\":2024,\"currency\":\"PLN\",\"revenue\":1200,"
                                + "\"ebit\":240,\"depreciation\":60,\"netProfit\":150,"
                                + "\"totalDebt\":310,\"equity\":600,\"operatingCashFlow\":140}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.revenueGrowthYoy").value(0.2000));
    }

    @Test
    void getCompany_ofAnotherUser_returns404() throws Exception {
        Cookie alice = login("alice@example.com");
        Cookie bob = login("bob@example.com");
        MvcResult created = mockMvc.perform(post("/companies").cookie(alice)
                        .contentType(APPLICATION_JSON).content(companyBody(2024, 1000, 200, 100)))
                .andExpect(status().isCreated()).andReturn();
        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/companies/" + id).cookie(bob))
                .andExpect(status().isNotFound());
    }
}
