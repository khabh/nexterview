package com.nexterview.server.repository;

import com.nexterview.server.domain.TokenQuota;
import com.nexterview.server.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenQuotaRepository extends JpaRepository<TokenQuota, Long> {

    Optional<TokenQuota> findByUser(User user);
}
