package com.project.finance.util;

import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.ApplyFileUploadLeaseRequest;
import com.aliyun.bailian20231229.models.ApplyFileUploadLeaseResponse;
import com.aliyun.tea.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class Sample {

    @Value("${app.upload.dir}")
    private static String uploadDir;

    /**
     * <b>description</b> :
     * <p>使用凭据初始化账号Client</p>
     * @return Client
     *
     * @throws Exception
     */
    public static Client createClient() throws Exception {
        // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        Config config = new Config()
                .setCredential(credential);
        // Endpoint 请参考 https://api.aliyun.com/product/bailian
        config.endpoint = "bailian.cn-beijing.aliyuncs.com";
        return new Client(config);
    }

    public static void main(String[] args) throws Exception {
//        String path = uploadDir + "e39ca43c-0a89-46e6-bc45-9eb8f2c5eba3.png";
        String path = "E:/DeliveryOptimization/project/code/uploads/e39ca43c-0a89-46e6-bc45-9eb8f2c5eba3.png";
        String md5 = Md5Utils.getFileMd5(path);
        String fileSize = null;

        if (ImageSizeValidator.isValidImageSize(path)) {
            fileSize = ImageSizeValidator.getImageSizeInBytes(path);
            if (fileSize == null) {
                System.out.println("图片大小不合法");
            }
            System.out.println("图片大小合法");
        } else {
            System.out.println("图片大小不合法");
        }

        Client client = Sample.createClient();
        ApplyFileUploadLeaseRequest applyFileUploadLeaseRequest = new ApplyFileUploadLeaseRequest()
                .setFileName("e39ca43c-0a89-46e6-bc45-9eb8f2c5eba3.png")
                .setMd5(md5)
                .setSizeInBytes(fileSize);
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            ApplyFileUploadLeaseResponse resp = client.applyFileUploadLeaseWithOptions("default", "SESSION_FILE", applyFileUploadLeaseRequest, headers, runtime);
            System.out.println(new Gson().toJson(resp));
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
        }
    }
}
