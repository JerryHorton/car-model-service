package cn.cug.sxy.infrastructure.minio;

import cn.cug.sxy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/4 19:50
 * @Description 文件上传工具类
 * @Author jerryhotton
 */

@Slf4j
public class FileUploadUtil {

    /**
     * 支持的图片MIME类型
     */
    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml"
    );

    /**
     * 支持的图片文件扩展名
     */
    private static final List<String> SUPPORTED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"
    );

    /**
     * 默认最大文件大小（10MB）
     */
    private static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 校验图片文件
     *
     * @param file 文件
     * @throws cn.cug.sxy.types.exception.AppException 校验失败时抛出异常
     */
    public static void validateImageFile(MultipartFile file) {
        validateImageFile(file, DEFAULT_MAX_FILE_SIZE);
    }

    /**
     * 校验图片文件
     *
     * @param file        文件
     * @param maxFileSize 最大文件大小
     * @throws cn.cug.sxy.types.exception.AppException 校验失败时抛出异常
     */
    public static void validateImageFile(MultipartFile file, long maxFileSize) {
        if (file == null || file.isEmpty()) {
            throw new AppException("文件不能为空");
        }
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new AppException(String.format("文件大小不能超过%dMB", maxFileSize / (1024 * 1024)));
        }
        // 检查MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException("不支持的文件类型，请上传jpg、png、gif、svg等图片格式");
        }
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException("文件名不能为空");
        }
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!SUPPORTED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new AppException("不支持的文件扩展名，请上传jpg、png、gif、svg等图片格式");
        }
        // 特殊处理SVG文件
        if (".svg".equals(extension) || "image/svg+xml".equals(contentType)) {
            validateSvgFile(file);
        }
        log.info("文件校验通过: {}, 大小: {}KB, 类型: {}",
                originalFilename, file.getSize() / 1024, contentType);
    }

    /**
     * 检查是否为有效的图片文件
     *
     * @param file 文件
     * @return 是否为有效图片
     */
    public static boolean isValidImageFile(MultipartFile file) {
        try {
            validateImageFile(file);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名（包含点号）
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 获取文件名（不包含扩展名）
     *
     * @param fileName 文件名
     * @return 文件名（不包含扩展名）
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return fileName;
        }

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 生成安全的文件名
     *
     * @param originalFileName 原始文件名
     * @return 安全的文件名
     */
    public static String generateSafeFileName(String originalFileName) {
        if (originalFileName == null) {
            return "unknown";
        }
        // 移除特殊字符，只保留字母、数字、点号、下划线和连字符
        String safeName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        // 确保文件名不超过255个字符
        if (safeName.length() > 255) {
            String extension = getFileExtension(safeName);
            String nameWithoutExt = getFileNameWithoutExtension(safeName);
            safeName = nameWithoutExt.substring(0, 255 - extension.length()) + extension;
        }

        return safeName;
    }

    /**
     * 验证SVG文件
     *
     * @param file SVG文件
     * @throws AppException 验证失败时抛出异常
     */
    public static void validateSvgFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new AppException("SVG文件内容为空");
            }
            // 检查是否包含SVG标签
            String content;
            boolean hasSvgTag = false;
            // 读取前几行来检查SVG标签
            for (int i = 0; i < 10 && firstLine != null; i++) {
                content = firstLine.toLowerCase();
                if (content.contains("<svg") || content.contains("<?xml")) {
                    hasSvgTag = true;
                    break;
                }
                firstLine = reader.readLine();
            }
            if (!hasSvgTag) {
                throw new AppException("不是有效的SVG文件格式");
            }
            log.info("SVG文件验证通过: {}", file.getOriginalFilename());

        } catch (IOException e) {
            log.error("读取SVG文件失败", e);
            throw new AppException("SVG文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否为SVG文件
     *
     * @param file 文件
     * @return 是否为SVG文件
     */
    public static boolean isSvgFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return "image/svg+xml".equals(contentType) ||
                (fileName != null && fileName.toLowerCase().endsWith(".svg"));
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 从Base64数据创建MultipartFile
     *
     * @param base64Data  Base64编码数据
     * @param fileName    文件名
     * @param contentType 文件类型
     * @return MultipartFile
     */
    public static MultipartFile createMultipartFileFromBase64(String base64Data, String fileName, String contentType) {
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);

        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return fileBytes.length == 0;
            }

            @Override
            public long getSize() {
                return fileBytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return fileBytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(fileBytes);
            }

            @Override
            public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                throw new UnsupportedOperationException("transferTo not supported");
            }
        };
    }

}
