package com.nexterview.server.service;

import com.nexterview.server.domain.Prompt;
import com.nexterview.server.domain.PromptQuery;
import com.nexterview.server.repository.PromptQueryRepository;
import com.nexterview.server.repository.PromptRepository;
import com.nexterview.server.service.dto.response.PromptDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final PromptQueryRepository promptQueryRepository;

    public List<PromptDto> findAll() {
        return promptRepository.findAll()
                .stream()
                .map(this::promptToDto)
                .toList();
    }

    private PromptDto promptToDto(Prompt prompt) {
        List<PromptQuery> promptQueries = promptQueryRepository.findAllByPrompt(prompt);
        return PromptDto.of(prompt, promptQueries);
    }
}
