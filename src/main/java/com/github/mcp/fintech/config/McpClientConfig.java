package com.github.mcp.fintech.config;

import com.github.mcp.fintech.FintechAssistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class McpClientConfig {

    @Bean
    public FintechAssistant fintechAssistant() {
        var model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        var mcpClient = getMcpClient();

        var toolProvider = McpToolProvider.builder()
                .mcpClients(List.of(mcpClient))
                .build();

        return AiServices.builder(FintechAssistant.class)
                .chatLanguageModel(model)
                .toolProvider(toolProvider)
                .build();
    }

    @Bean
    public DefaultMcpClient getMcpClient() {
        var transport = new HttpMcpTransport.Builder()
                .sseUrl("http://localhost:8080/sse")
                .timeout(Duration.ofMinutes(2))
                .logRequests(true) // if you want to see the traffic in the log
                .logResponses(true)
                .build();

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(Duration.ofDays(365))
                .pingTimeout(Duration.ofDays(365))
                .resourcesTimeout(Duration.ofDays(365))
                .promptsTimeout(Duration.ofDays(365))
                .build();
    }

    @Scheduled(fixedRate = 60000) // ping every minute
    public void ping() {
        log.info("Executing scheduled ping...");
        try {
            var mcpClient = getMcpClient();
            mcpClient.checkHealth();
        } catch (Exception e) {
            log.error("Error pinging MCP client: {}", e.getMessage());
        }
    }
}
