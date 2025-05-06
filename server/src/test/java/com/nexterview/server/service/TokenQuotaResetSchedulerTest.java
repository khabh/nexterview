package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexterview.server.domain.TokenQuota;
import com.nexterview.server.domain.User;
import com.nexterview.server.repository.TokenQuotaRepository;
import com.nexterview.server.util.DatabaseCleaner;
import com.nexterview.server.util.UserFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenQuotaResetSchedulerTest {

    @Autowired
    private TokenQuotaRepository tokenQuotaRepository;

    @Autowired
    private TokenQuotaResetScheduler scheduler;

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    @Test
    void resetAllUserQuotas_shouldResetAllRemainingToMax() {
        User user1 = userFixture.getSavedUser("user1@example.com", "user111", "12345678");
        User user2 = userFixture.getSavedUser("user2@example.com", "user222", "12345678");

        tokenQuotaRepository.save(new TokenQuota(user1, 100));
        tokenQuotaRepository.save(new TokenQuota(user2, 50));

        scheduler.resetAllUserQuotas();

        List<TokenQuota> all = tokenQuotaRepository.findAll();

        assertThat(all).allMatch(q -> q.getRemainingQuota() == TokenQuota.MAX_REMAINING_QUOTA);
    }
}
