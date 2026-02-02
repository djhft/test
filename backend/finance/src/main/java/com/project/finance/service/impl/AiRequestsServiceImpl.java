package com.project.finance.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.project.finance.entity.AiRequests;
import com.project.finance.entity.Transactions;
import com.project.finance.mapper.AiRequestsMapper;
import com.project.finance.service.IAiRequestsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.finance.service.ICategoriesService;
import com.project.finance.service.ITransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.math.BigDecimal;
import com.project.finance.prompt.chatPrompt;

@Service
public class AiRequestsServiceImpl extends ServiceImpl<AiRequestsMapper, AiRequests> implements IAiRequestsService {

    @Autowired
    private ITransactionsService transactionService;

    @Autowired
    private ICategoriesService categoriesService;

    // 从配置文件注入 API Key
    @Value("${zhipu.api.key:31d78753095543268d56372930b1e2e7.W7nfC2wmt2iGToYV}")
    private String API_KEY;

    private static final String MODEL_NAME = "glm-4.7-flash";

    @Override
    public String getAnswer(Long uid, List<ChatMessage> chatHistory, String startDate, String endDate) {
        try {
            List<Transactions> transactions = transactionService.findByUserIdAndDateBetween(uid, startDate, endDate);

            //  构建财务数据分析上下文
            String financialContext = buildFinancialContext(transactions, startDate, endDate);

            //  初始化客户端
            ZhipuAiClient client = ZhipuAiClient.builder().ofZHIPU()
                    .apiKey(API_KEY)
                    .build();

            //  构建最终的消息列表 (避免直接修改传入的 chatHistory)
            List<ChatMessage> messagesToSend = new ArrayList<>();


            if (chatHistory == null) {
                // 6.1 添加 System Prompt
                messagesToSend.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM.value()) // 修复：直接使用字符串 "system"
                        .content(chatPrompt.imageRecognitionPrompt)
                        .build());

                // 6.2 将财务数据作为上下文添加
                messagesToSend.add(ChatMessage.builder()
                        .role(ChatMessageRole.USER.value()) // 修复：直接使用字符串 "user"
                        .content("以下是需要分析的财务数据:\n" + financialContext)
                        .build());
            }

            // 6.3 添加历史对话记录
            // 注意：确保传入的 chatHistory 中的 message 的 role 也是符合要求的格式 (如 "user", "assistant")
            if (chatHistory != null) {
                messagesToSend.addAll(chatHistory);
            }

            // 7. 创建聊天完成请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model(MODEL_NAME)
                    .messages(messagesToSend)
                    .build();

            // 8. 发送请求
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

            // 9. 处理响应
            if (response.isSuccess()) {
                ChatMessage reply = response.getData().getChoices().get(0).getMessage();
                System.out.println("AI 回复: " + reply.getContent());

                // 注意：这里不需要手动添加到 chatHistory，
                // 通常由前端或调用方管理历史记录的持久化。
                // 如果后端需要管理，建议返回完整的 ChatMessage 对象给 Controller 处理。

                return (String) reply.getContent();
            } else {
                System.err.println("API 调用失败: " + response.getMsg());
                return "抱歉，AI 服务暂时不可用，请稍后再试。";
            }
        } catch (Exception e) {
            System.err.println("AI分析过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            return "系统繁忙，请稍后再试。";
        }
    }

    /**
     * 构建财务数据上下文，用于AI分析
     */
    private String buildFinancialContext(List<Transactions> transactions, String startDate, String endDate) {
        // 初始化变量
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> incomeCategories = new HashMap<>();
        Map<String, BigDecimal> expenseCategories = new HashMap<>();

        // 遍历交易记录
        for (Transactions tx : transactions) {
            // 检查交易类型是否为收入
            if ("INCOME".equals(tx.getType())) {
                // 累加总收入
                totalIncome = totalIncome.add(tx.getAmount());

                // 获取分类名称
                String categoryName = categoriesService.typechange(tx.getCategoryId());

                // 累加该分类的收入金额
                incomeCategories.merge(categoryName, tx.getAmount(), BigDecimal::add);
            } else {
                // 累加总支出
                totalExpense = totalExpense.add(tx.getAmount());

                // 获取分类名称
                String categoryName = categoriesService.typechange(tx.getCategoryId());

                // 累加该分类的支出金额
                expenseCategories.merge(categoryName, tx.getAmount(), BigDecimal::add);
            }
        }

        // 构建上下文字符串
        StringBuilder context = new StringBuilder();
        context.append(String.format("分析期间: %s 至 %s\n", startDate, endDate));
        context.append(String.format("总交易数量: %d 笔\n", transactions.size()));
        context.append(String.format("总收入: %.2f 元\n", totalIncome.doubleValue()));
        context.append(String.format("总支出: %.2f 元\n", totalExpense.doubleValue()));
        context.append(String.format("净结余: %.2f 元\n\n", totalIncome.subtract(totalExpense).doubleValue()));

        // 收入分类
        if (!incomeCategories.isEmpty()) {
            context.append("收入分类分布:\n");
            incomeCategories.forEach((category, amount) ->
                    context.append(String.format("- %s: %.2f 元\n", category, amount.doubleValue()))
            );
            context.append("\n");
        }

        // 支出分类
        if (!expenseCategories.isEmpty()) {
            context.append("支出分类分布:\n");
            expenseCategories.forEach((category, amount) ->
                    context.append(String.format("- %s: %.2f 元\n", category, amount.doubleValue()))
            );
            context.append("\n");
        }

        // 最近的大额交易（前5笔）
        context.append("大额交易记录（前5笔）:\n");
        transactions.stream()
                .sorted((t1, t2) -> t2.getAmount().compareTo(t1.getAmount()))
                .limit(5)
                .forEach(tx -> context.append(String.format("- %s %s: %.2f 元 (%s)\n",
                        tx.getDate(),
                        "INCOME".equals(tx.getType()) ? "收入-" + categoriesService.typechange(tx.getCategoryId()) : "支出-" + categoriesService.typechange(tx.getCategoryId()),
                        tx.getAmount().doubleValue(),
                        tx.getTags() != null ? tx.getTags() : "无标签")));

        System.out.println(context);
        return context.toString();
    }
}