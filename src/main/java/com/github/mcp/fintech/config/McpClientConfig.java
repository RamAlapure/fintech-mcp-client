package com.github.mcp.fintech.config;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mcp.fintech.FintechAssistant;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

@Configuration
public class McpClientConfig {

	@Bean
	public FintechAssistant fintechAssistant() {
		var model = OpenAiChatModel.builder()
				.baseUrl("http://langchain4j.dev/demo/openai/v1")
				.apiKey("demo")
				.modelName("gpt-4o-mini")
				.build();

		var transport = new HttpMcpTransport.Builder()
				.sseUrl("http://localhost:8080/sse")
				.timeout(Duration.ofDays(1))
				.logRequests(true) // if you want to see the traffic in the log
				.logResponses(true)
				.build();

		var mcpClient = new DefaultMcpClient.Builder()
				.transport(transport)
				.toolExecutionTimeout(Duration.ofDays(365))
				.pingTimeout(Duration.ofDays(365))
				.resourcesTimeout(Duration.ofDays(365))
				.promptsTimeout(Duration.ofDays(365))
				.build();

		var toolProvider = McpToolProvider.builder()
				.mcpClients(List.of(mcpClient))
				.build();

		return AiServices.builder(FintechAssistant.class)
				.chatLanguageModel(model)
				.toolProvider(toolProvider)
				.build();
	}
}
