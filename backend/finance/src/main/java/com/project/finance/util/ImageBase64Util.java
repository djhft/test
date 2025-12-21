package com.project.finance.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * 图片转 Base64 Data URL 工具类
 * 适用于 DashScope / Qwen-VL 等多模态模型的图片输入
 */
public class ImageBase64Util {

    /**
     * 将本地图片文件转换为 Data URL 格式的 Base64 字符串
     * 支持常见格式：jpg, jpeg, png, gif, webp, bmp
     *
     * @param imagePath 图片文件路径（如 "./photo.jpg"）
     * @return Data URL 字符串，例如 "data:image/jpeg;base64,/9j/4AAQSk..."
     * @throws IOException 文件不存在、读取失败等
     * @throws IllegalArgumentException 不支持的文件扩展名
     */
//    public static String imageToBase64DataUrl(String imagePath) throws IOException {
//        return imageToBase64DataUrl(Path.of(imagePath));
//    }

    /**
     * 将 Path 对象表示的图片文件转换为 Data URL 格式的 Base64 字符串
     *
     * @param imagePath 图片文件路径（Path 对象）
     * @return Data URL 字符串
     * @throws IOException 文件读取异常
     * @throws IllegalArgumentException 不支持的文件类型
     */
    public static String imageToBase64DataUrl(Path imagePath) throws IOException {
        if (!Files.exists(imagePath)) {
            throw new IOException("图片文件不存在: " + imagePath.toAbsolutePath());
        }

        // 获取文件扩展名（转小写）
        String fileName = imagePath.getFileName().toString().toLowerCase();
        String mimeType = getMimeType(fileName);

        if (mimeType == null) {
            throw new IllegalArgumentException("不支持的图片格式: " + fileName +
                    "。支持格式: jpg, jpeg, png, gif, webp, bmp");
        }

        // 读取文件字节并编码
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        return "data:" + mimeType + ";base64," + base64;
    }

    /**
     * 根据文件名推断 MIME 类型
     */
    private static String getMimeType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".bmp")) {
            return "image/bmp";
        }
        return null;
    }
}
