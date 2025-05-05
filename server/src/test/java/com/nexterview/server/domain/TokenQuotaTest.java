package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.util.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TokenQuotaTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 222_222})
    void 남은_쿼타가_유효하면_예외가_발생하지_않는다(int remainingQuota) {
        User user = UserFixture.createUser("test@example.com", "testUser");
        TokenQuota tokenQuota = new TokenQuota(user, remainingQuota);

        assertThatCode(tokenQuota::validateQuotaAvailable)
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 222_223})
    void 남은_쿼타가_유효하지_않으면_예외를_던진다(int remainingQuota) {
        User user = UserFixture.createUser("test@example.com", "testUser");

        assertThatThrownBy(() -> new TokenQuota(user, remainingQuota))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.TOKEN_QUOTA_REMAINING_QUOTA_INVALID.getMessage());
    }

    @Test
    void 쿼타를_사용하고_남은_쿼타가_0_이하로_떨어지면_예외를_던진다() {
        User user = UserFixture.createUser("test@example.com", "testUser");
        TokenQuota tokenQuota = TokenQuota.createMaxQuota(user);

        tokenQuota.useQuota(222_222);

        if (tokenQuota.isDepleted()) {
            assertThatThrownBy(tokenQuota::validateQuotaAvailable)
                    .isInstanceOf(NexterviewException.class)
                    .hasMessageContaining(NexterviewErrorCode.USER_PROMPT_ACCESS_EXCEEDED.getMessage());
        }
    }
}
