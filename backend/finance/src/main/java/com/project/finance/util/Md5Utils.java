package com.project.finance.util;

// 示例代码仅供参考，请勿在生产环境中直接使用
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Md5Utils {

    static String getFileMd5(String filePath) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] md5Bytes = digest.digest();

        StringBuilder md5String = new StringBuilder();
        for (byte b : md5Bytes) {
            md5String.append(String.format("%02x", b));
        }

        return md5String.toString();
    }

}
