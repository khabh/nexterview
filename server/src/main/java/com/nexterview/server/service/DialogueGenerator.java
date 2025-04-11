package com.nexterview.server.service;

import com.nexterview.server.domain.CustomizedPrompt;

public interface DialogueGenerator {

    GeneratedDialogues generate(CustomizedPrompt customizedPrompt);
}
