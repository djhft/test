package com.project.finance.util;

//import com.alibaba.dashscope.DashScope;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.project.finance.DTO.TransactionDTO;
import com.project.finance.prompt.imageRecognitionPrompt;
import org.springframework.beans.factory.annotation.Value;
//import com.alibaba.dashscope.multimodal.MultiModalParam;
//import com.alibaba.dashscope.multimodal.MultiModalResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ImageRecognition {

    @Value("${app.api.key}")
    private String apiKey;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static String encodeImageToBase64(Path imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imagePath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static TransactionDTO imageRecognition(Path path) throws NoApiKeyException, UploadFileException, IOException {
        String base64Image = encodeImageToBase64(path);
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        new HashMap<String, Object>() {{ put("image", "data:image/png;base64," + base64Image); }},
                        new HashMap<String, Object>() {{ put("text", imageRecognitionPrompt.imageRecognitionPrompt); }}
                )).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey("sk-85c524b8b1624ceaabc3b0d057359aff")
                .model("qwen3-vl-plus")  // 此处以qwen3-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/models
                .messages(Arrays.asList(userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        String json = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
        TransactionDTO transaction = JsonUtil.fromJson(json, TransactionDTO.class);
        return transaction;
    }

}
