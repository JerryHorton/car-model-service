package cn.cug.sxy.infrastructure.minio;

import cn.cug.sxy.types.enums.TemplateFileType;
import cn.cug.sxy.types.exception.AppException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Date 2025/8/4 19:46
 * @Description MinIO文件存储服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class MinIOFileStorageService implements IFileStorageService {

    private final MinioClient minioClient;

    public MinIOFileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Value("${minio.bucket-name:car-model-service}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 支持的图片格式
     */
    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml"
    );

    /**
     * 支持的文件扩展名
     */
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"
    );

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostConstruct
    public void init() {
        log.info("MinIO文件存储服务初始化，bucket={}, endpoint={}", bucketName, endpoint);
    }

    @Override
    public String uploadExplodedViewImage(MultipartFile file, Long usageId, Long groupId) {
        try {
            log.info("开始上传爆炸图，usageId={}, groupId={}, fileName={}", usageId, groupId, file.getOriginalFilename());
            // 文件校验
            validateImageFile(file);
            String objectName = generateExplodedViewPath(usageId, groupId, file.getOriginalFilename());
            // 上传文件到MinIO
            try (InputStream inputStream = file.getInputStream()) {
                // 确保SVG文件有正确的Content-Type
                String contentType = file.getContentType();
                if (objectName.toLowerCase().endsWith(".svg") &&
                        (contentType == null || !contentType.equals("image/svg+xml"))) {
                    contentType = "image/svg+xml";
                }
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(contentType)
                                .build()
                );
            }
            // 生成文件URL
            String fileUrl = generateFileUrl(objectName);
            log.info("爆炸图上传成功 usageId={}, groupId={}, fileName={}, fileUrl={}",
                    usageId, groupId, file.getOriginalFilename(), fileUrl);

            return fileUrl;
        } catch (Exception e) {
            log.error("上传爆炸图失败", e);
            throw new AppException("上传爆炸图失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadModelIcon(String fileData, String fileName, String contentType, Long modelId) {
        try {
            if (!contentType.startsWith("image/")) {
                throw new AppException("上传文件必须是图片格式");
            }
            // 将Base64数据转换为MultipartFile
            MultipartFile file = FileUploadUtil.createMultipartFileFromBase64(
                    fileData,
                    fileName,
                    contentType
            );
            log.info("开始上传车型图标，modelId={}, fileName={}", modelId, fileName);
            // 文件校验
            validateImageFile(file);
            // 生成存储路径
            String objectName = generateModelIconPath(modelId, file.getOriginalFilename());
            // 上传文件到MinIO
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(contentType)
                                .build()
                );
            }
            // 生成文件URL
            String fileUrl = generateFileUrl(objectName);
            log.info("车型图标上传成功，modelId={}, fileName={}, fileUrl={}",
                    modelId, fileName, fileUrl);

            return fileUrl;
        } catch (Exception e) {
            log.error("车型图标上传失败，modelId={}, fileName={}", modelId, fileName, e);
            throw new AppException("车型图标上传失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadTemplate(MultipartFile file, String templateFileType) {
        try {
            log.info("开始上传上传模板 fileName={}, fileSize={}",
                    file.getOriginalFilename(), file.getSize());
            // 文件校验
            validateTemplateFile(file);
            // 生成模板存储路径
            String objectName = TemplateFileType.fromType(templateFileType).generateTemplatePath();
            // 上传文件到MinIO
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                .build()
                );
            }
            log.info("模板上传成功 objectName={}", objectName);

            return String.format("模板上传成功，存储路径: %s", objectName);
        } catch (Exception e) {
            log.error("上传工时批量上传模板失败", e);
            throw new RuntimeException("上传模板失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            if (StringUtils.isBlank(fileUrl)) {
                return true;
            }
            log.info("开始删除文件，URL: {}", fileUrl);
            // 从URL中提取对象名称
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                log.warn("无法从URL中提取对象名称: {}", fileUrl);
                return false;
            }
            // 删除MinIO中的文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("文件删除成功，objectName: {}", objectName);

            return true;
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return false;
        }
    }

    @Override
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        // 检查MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        String extension = getFileExtension(originalFilename).toLowerCase();

        return SUPPORTED_EXTENSIONS.contains(extension);
    }

    @Override
    public String generateExplodedViewPath(Long usageId, Long groupId, String fileName) {
        // 生成路径格式: exploded-views/group_{groupId}/usage_{usageId}/yyyyMMdd/uuid_originalName
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(fileName);
        String newFileName = uuid + extension;

        return String.format("exploded-views/group_%d/usage_%d/%s/%s",
                groupId, usageId, datePath, newFileName);
    }

    @Override
    public String generateModelIconPath(Long modelId, String fileName) {
        // 生成路径格式: model-icons/brand_{brand}/model_{modelId}/yyyyMMdd/uuid_originalName
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(fileName);
        String newFileName = uuid + extension;
        // 如果modelId为null，使用临时标识
        String modelPath = modelId != null ? "model_" + modelId : "temp_" + uuid.substring(0, 8);

        return String.format("model-icons/%s/%s/%s",
                modelPath, datePath, newFileName);
    }

    @Override
    public String generatePresignedUrl(String path, int expiryDuration, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .expiry(expiryDuration, timeUnit)
                            .method(Method.GET)
                            .build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
    }

    @Override
    public String getTemplateFileInfo(String templateFileType) {
        String templatePath = TemplateFileType.fromType(templateFileType).generateTemplatePath();
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(templatePath)
                            .build()
            );

            return String.format("模板文件: %s, 大小: %d bytes, 最后修改: %s",
                    templatePath,
                    stat.size(),
                    stat.lastModified());
        } catch (Exception e) {
            log.warn("获取模板信息失败", e);
            return "模板文件不存在或获取信息失败";
        }
    }

    public boolean isTemplateExists(String templateFileType) {
        String templatePath = TemplateFileType.fromType(templateFileType).generateTemplatePath();
        try {
            minioClient.statObject(
                    io.minio.StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(templatePath)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.warn("模板文件不存在 bucket={}, key={}", bucketName, templatePath);
            return false;
        }
    }

    @Override
    public byte[] getTemplateFile(String templateFileType) throws IOException {
        String templatePath = TemplateFileType.fromType(templateFileType).generateTemplatePath();
        try {
            log.info("从MinIO获取模板 bucket={}, key={}", bucketName, templatePath);
            // 从MinIO获取模板文件
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(templatePath)
                            .build()
            );
            // 读取输入流到字节数组
            byte[] templateData = inputStream.readAllBytes();
            inputStream.close();
            log.info("成功从MinIO获取模板，文件大小: {} bytes", templateData.length);

            return templateData;
        } catch (MinioException e) {
            log.error("从MinIO获取模板失败", e);
            throw new IOException("获取模板失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("读取模板异常", e);
            throw new IOException("读取模板异常: " + e.getMessage(), e);
        }
    }

    /**
     * 校验图片文件
     *
     * @param file 文件
     */
    private void validateImageFile(MultipartFile file) {
        FileUploadUtil.validateImageFile(file, MAX_FILE_SIZE);
    }

    /**
     * 生成文件访问URL
     *
     * @param objectName 对象名称
     * @return 访问URL
     */
    private String generateFileUrl(String objectName) {
        // 生成MinIO访问URL
        return String.format("%s/%s/%s", endpoint, bucketName, objectName);
    }

    /**
     * 从URL中提取对象名称
     *
     * @param fileUrl 文件URL
     * @return 对象名称
     */
    private String extractObjectNameFromUrl(String fileUrl) {
        try {
            String prefix = String.format("%s/%s/", endpoint, bucketName);
            if (fileUrl.startsWith(prefix)) {
                return fileUrl.substring(prefix.length());
            }
            return null;
        } catch (Exception e) {
            log.error("提取对象名称失败", e);
            return null;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名（包含点号）
     */
    private String getFileExtension(String fileName) {
        return FileUploadUtil.getFileExtension(fileName);
    }

    /**
     * 校验模板文件
     *
     * @param file 文件
     */
    private void validateTemplateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("只支持Excel文件格式(.xlsx/.xls)");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("模板文件大小不能超过2MB");
        }
    }


}
