package com.nexterview.server.repository;

import com.nexterview.server.domain.TokenQuota;
import com.nexterview.server.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenQuotaRepository extends JpaRepository<TokenQuota, Long> {

    Optional<TokenQuota> findByUser(User user);

    @Modifying
    @Query("UPDATE TokenQuota tq SET tq.remainingQuota = :maxQuota")
    void updateAllQuotas(@Param("maxQuota") int maxQuota);
}
