package com.nexterview.server.repository;

import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptQuery;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptQueryRepository extends JpaRepository<PromptQuery, Long> {

    List<PromptQuery> findAllByPrompt(Prompt prompt);
}
