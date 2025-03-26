package com.nexterview.server.repository;

import com.nexterview.server.domain.PromptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptAnswerRepository extends JpaRepository<PromptAnswer, Long> {
}
