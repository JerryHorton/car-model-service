package cn.cug.sxy.infrastructure.minio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Date 2025/8/4 19:45
 * @Description 文件存储服务接口
 * @Author jerryhotton
 */

public interface IFileStorageService {

    /**
     * 上传爆炸图文件
     *
     * @param file    文件
     * @param usageId 用法ID（用于生成文件路径）
     * @param groupId 分组ID（用于生成文件路径）
     * @return 文件访问URL
     */
    String uploadExplodedViewImage(MultipartFile file, Long usageId, Long groupId);

    /**
     * 上传车型图标
     *
     * @param fileData   文件数据
     * @param fileName   文件名
     * @param uploadType 上传类型
     * @param modelId    车型ID
     * @return 文件访问URL
     */
    String uploadModelIcon(String fileData, String fileName, String uploadType, Long modelId);

    /**
     * 上传工时批量上传模板
     *
     * @param file Excel模板文件
     * @return 上传结果信息
     */
    String uploadTemplate(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean delete(String fileUrl);

    /**
     * 检查文件是否为有效的图片格式
     *
     * @param file 文件
     * @return 是否为有效图片
     */
    boolean isValidImageFile(MultipartFile file);

    /**
     * 生成爆炸图文件存储路径
     *
     * @param usageId  用法ID
     * @param groupId  分组ID
     * @param fileName 原始文件名
     * @return 存储路径
     */
    String generateExplodedViewPath(Long usageId, Long groupId, String fileName);

    /**
     * 生成车型图标文件存储路径
     *
     * @param modelId  车型ID
     * @param fileName 原始文件名
     * @return 存储路径
     */
    String generateModelIconPath(Long modelId, String fileName);

    /**
     * 生成预签名URL
     *
     * @param path           文件路径
     * @param expiryDuration 过期时间
     * @param timeUnit       时间单位
     * @return 预签名URL
     */
    String generatePresignedUrl(String path, int expiryDuration, TimeUnit timeUnit);

    /**
     * 获取工时模板信息
     *
     * @return 模板信息
     */
    String getTemplateInfo();

    /**
     * 检查工时模板文件是否存在
     *
     * @return 是否存在
     */
    boolean isTemplateExists();

    /**
     * 获取工时模板文件
     *
     * @return 模板文件字节数组
     * @throws IOException 读取异常
     */
    byte[] getWorkHourTemplate() throws IOException;

}
