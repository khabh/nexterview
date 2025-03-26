package com.nexterview.server.repository;

import com.nexterview.server.domain.PromptQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptQueryRepository extends JpaRepository<PromptQuery, Long> {
}
