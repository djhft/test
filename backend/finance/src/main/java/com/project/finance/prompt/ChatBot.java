package com.project.finance.prompt;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.*;

public class ChatBot {
    private final ZhipuAiClient client;
    private final List<ChatMessage> conversation;

    public ChatBot(String apiKey) {
        this.client = ZhipuAiClient.builder().ofZHIPU()
                .apiKey("31d78753095543268d56372930b1e2e7.W7nfC2wmt2iGToYV")
                .build();
        this.conversation = new ArrayList<>();
        // 添加系统消息
        this.conversation.add(ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM.value())
                .content(chatPrompt.imageRecognitionPrompt)
                .build());
    }

    public Object chat(String userInput) {
        try {
            // 添加用户消息
            conversation.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(userInput)
                    .build());

            // 创建请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model("glm-4.7-flash")
                    .messages(conversation)
                    .temperature(1.0f)
                    .maxTokens(1000)
                    .build();

            // 发送请求
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

            if (response.isSuccess()) {
                // 获取 AI 回复
                Object aiResponse = response.getData().getChoices().get(0).getMessage().getContent();

                // 添加 AI 回复到对话历史
                conversation.add(ChatMessage.builder()
                        .role(ChatMessageRole.ASSISTANT.value())
                        .content(aiResponse)
                        .build());

                return aiResponse;
            } else {
                return "发生错误: " + response.getMsg();
            }

        } catch (Exception e) {
            return "发生错误: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        ChatBot bot = new ChatBot(System.getenv("ZAI_API_KEY"));
        Scanner scanner = new Scanner(System.in);

        System.out.println("欢迎使用 Z.ai 聊天机器人！输入 'quit' 退出。");

        while (true) {
            System.out.print("您: ");
            String input = scanner.nextLine();

            if ("quit".equalsIgnoreCase(input)) {
                break;
            }

            Object response = bot.chat(input);
            System.out.println("AI: " + response);
        }

        System.out.println("再见！");
        scanner.close();
    }
}
