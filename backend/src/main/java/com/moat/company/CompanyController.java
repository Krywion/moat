package com.moat.company;

import com.moat.company.dto.CompanyDetailResponse;
import com.moat.company.dto.CompanySummaryResponse;
import com.moat.company.dto.CreateCompanyRequest;
import com.moat.company.dto.FinancialForm;
import com.moat.company.dto.FinancialReportResponse;
import com.moat.esef.EsefParseException;
import com.moat.market.MarketDataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final MarketDataService marketDataService;

    public CompanyController(CompanyService companyService, MarketDataService marketDataService) {
        this.companyService = companyService;
        this.marketDataService = marketDataService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDetailResponse create(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody CreateCompanyRequest request) {
        return companyService.createCompany(userId(jwt), request);
    }

    @GetMapping
    public List<CompanySummaryResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return companyService.listCompanies(userId(jwt));
    }

    @GetMapping("/{id}")
    public CompanyDetailResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return companyService.getCompany(userId(jwt), id);
    }

    @PutMapping("/{id}/financials")
    public FinancialReportResponse updateFinancials(@AuthenticationPrincipal Jwt jwt,
                                                    @PathVariable UUID id,
                                                    @Valid @RequestBody FinancialForm form) {
        return companyService.updateFinancials(userId(jwt), id, form);
    }

    @PostMapping("/esef")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDetailResponse createFromEsef(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ticker", required = false) String ticker) {
        if (file == null || file.isEmpty()) {
            throw new EsefParseException("Empty ESEF file");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new EsefParseException("Cannot read uploaded file", e);
        }
        return companyService.createCompanyFromEsef(userId(jwt), bytes, ticker);
    }

    @PostMapping("/{id}/refresh-market")
    public CompanyDetailResponse refreshMarket(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        UUID ownerId = userId(jwt);
        marketDataService.refreshLatest(ownerId, id);
        return companyService.getCompany(ownerId, id);
    }

    private static UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
