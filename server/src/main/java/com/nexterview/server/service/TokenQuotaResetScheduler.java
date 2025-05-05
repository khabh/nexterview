package com.nexterview.server.service;

import com.nexterview.server.domain.TokenQuota;
import com.nexterview.server.repository.TokenQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TokenQuotaResetScheduler {

    private final TokenQuotaRepository tokenQuotaRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void resetAllUserQuotas() {
        tokenQuotaRepository.updateAllQuotas(TokenQuota.MAX_REMAINING_QUOTA);
    }
}
