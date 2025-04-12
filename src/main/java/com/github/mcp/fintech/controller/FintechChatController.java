package com.github.mcp.fintech.controller;

import com.github.mcp.fintech.FintechAssistant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api")
@RestController
public class FintechChatController {

    private final FintechAssistant fintechAssistant;

    public FintechChatController(FintechAssistant fintechAssistant) {
        this.fintechAssistant = fintechAssistant;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        log.info("Chat request received: {}", message);
        return fintechAssistant.chat(message);
    }
}
