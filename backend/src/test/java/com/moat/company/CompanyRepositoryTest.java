package com.moat.company;

import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import com.moat.user.Role;
import com.moat.user.User;
import com.moat.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CompanyRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired CompanyRepository companyRepository;
    @Autowired FinancialReportRepository reportRepository;
    @Autowired UserRepository userRepository;

    private User newUser(String email) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash("hash");
        u.setRole(Role.USER);
        u.setCreatedAt(OffsetDateTime.now());
        return userRepository.save(u);
    }

    private Company newCompany(User owner, String name) {
        Company c = new Company();
        c.setOwner(owner);
        c.setName(name);
        c.setCreatedAt(OffsetDateTime.now());
        return companyRepository.save(c);
    }

    @Test
    void findByOwnerId_returnsOnlyOwnersCompanies() {
        User alice = newUser("alice@example.com");
        User bob = newUser("bob@example.com");
        newCompany(alice, "Alice Co");
        newCompany(bob, "Bob Co");

        assertThat(companyRepository.findByOwnerId(alice.getId())).hasSize(1);
        assertThat(companyRepository.findByIdAndOwnerId(
                companyRepository.findByOwnerId(bob.getId()).get(0).getId(), alice.getId()))
                .isEmpty();
    }

    @Test
    void reportQueries_byCompanyAndYear() {
        Company c = newCompany(newUser("carol@example.com"), "Carol Co");
        reportRepository.save(report(c, 2023));
        reportRepository.save(report(c, 2024));

        assertThat(reportRepository.findByCompanyIdAndFiscalYear(c.getId(), 2023)).isPresent();
        assertThat(reportRepository.findByCompanyIdOrderByFiscalYearAsc(c.getId()))
                .extracting(FinancialReport::getFiscalYear).containsExactly(2023, 2024);
        assertThat(reportRepository.findFirstByCompanyIdOrderByFiscalYearDesc(c.getId()))
                .get().extracting(FinancialReport::getFiscalYear).isEqualTo(2024);
    }

    private FinancialReport report(Company c, int year) {
        FinancialReport r = new FinancialReport();
        r.setCompany(c);
        r.setFiscalYear(year);
        r.setCurrency("PLN");
        r.setCreatedAt(OffsetDateTime.now());
        return r;
    }
}
