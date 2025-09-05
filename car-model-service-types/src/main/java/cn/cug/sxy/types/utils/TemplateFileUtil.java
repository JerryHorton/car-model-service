package cn.cug.sxy.types.utils;

import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version 1.0
 * @Date 2025/9/4 09:20
 * @Description 模板文件工具类
 * @Author jerryhotton
 */

public class TemplateFileUtil {

    public static void validateTemplateFile(MultipartFile file) {
        // 验证文件
        if (file.isEmpty()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "上传文件不能为空");
        }
        // 验证文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "只支持Excel文件格式(.xlsx/.xls)");
        }
        // 验证文件大小（限制为2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "模板文件大小不能超过2MB");
        }
    }

}
