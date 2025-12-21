package com.project.finance.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 图片大小校验工具类
 */
public class ImageSizeValidator {

    // 100 MB = 100 * 1024 * 1024 字节
    private static final long MAX_SIZE_BYTES = 100L * 1024 * 1024;
    private static final long MIN_SIZE_BYTES = 1L;

    /**
     * 判断指定路径的图片文件大小是否在 [1B, 100MB] 范围内
     *
     * @param imagePath 图片文件路径
     * @return true 如果大小合法；否则 false
     */
    public static boolean isValidImageSize(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        return isValidImageSize(new File(imagePath));
    }

    /**
     * 判断指定 File 对象的图片文件大小是否在 [1B, 100MB] 范围内
     *
     * @param imageFile 图片文件对象
     * @return true 如果大小合法；否则 false
     */
    public static boolean isValidImageSize(File imageFile) {
        if (imageFile == null || !imageFile.exists() || imageFile.isDirectory()) {
            return false;
        }

        try {
            long fileSize = Files.size(imageFile.toPath());
            return fileSize >= MIN_SIZE_BYTES && fileSize <= MAX_SIZE_BYTES;
        } catch (Exception e) {
            // 文件不存在、无权限等异常情况视为无效
            return false;
        }
    }

    /**
     * 获取文件大小（单位：字节），如果文件无效则返回 -1
     *
     * @param imageFile 图片文件对象
     * @return 文件大小（字节）或 -1
     */
    /**
     * 获取文件大小（单位：字节），如果文件无效则返回 -1
     *
     * @param imagePath 图片文件路径
     * @return 文件大小（字节）或 -1
     */
    public static String getImageSizeInBytes(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        File imageFile = new File(imagePath);
        return String.valueOf(imageFile.length());
    }

    // 示例用法
    public static void main(String[] args) {
        String path = "example.jpg";
        if (isValidImageSize(path)) {
            System.out.println("图片大小合法");
        } else {
            System.out.println("图片大小不合法");
        }
    }
}
