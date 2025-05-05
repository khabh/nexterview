package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.Range;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TokenQuota {

    public static final int MAX_REMAINING_QUOTA = 222_222;
    public static final Range QUOTA_RANGE = new Range(0, MAX_REMAINING_QUOTA);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private int remainingQuota;

    public TokenQuota(User user, int remainingQuota) {
        validateUser(user);
        validateRemainingQuota(remainingQuota);
        this.user = user;
        this.remainingQuota = remainingQuota;
    }

    public static TokenQuota createMaxQuota(User user) {
        return new TokenQuota(user, MAX_REMAINING_QUOTA);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new NexterviewException(NexterviewErrorCode.USER_NULL);
        }
    }

    private void validateRemainingQuota(int remainingQuota) {
        if (QUOTA_RANGE.outOfRange(remainingQuota)) {
            throw new NexterviewException(NexterviewErrorCode.TOKEN_QUOTA_REMAINING_QUOTA_INVALID);
        }
    }

    public void validateQuotaAvailable() {
        if (isDepleted()) {
            throw new NexterviewException(NexterviewErrorCode.USER_PROMPT_ACCESS_EXCEEDED);
        }
    }

    public boolean isDepleted() {
        return remainingQuota <= 0;
    }

    public void useQuota(int amount) {
        remainingQuota = Math.max(0, remainingQuota - amount);
    }
}
