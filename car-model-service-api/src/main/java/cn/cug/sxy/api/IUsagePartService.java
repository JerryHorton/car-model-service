package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.ClearPartsRequestDTO;
import cn.cug.sxy.api.dto.UnBindPartRequestDTO;
import cn.cug.sxy.api.dto.UsageBindPartRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.UsageBindPartResultVO;
import cn.cug.sxy.api.vo.UsagePartVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/5 10:50
 * @Description 用法备件关联服务接口
 * @Author jerryhotton
 */

public interface IUsagePartService {

    /**
     * 绑定备件到用法
     *
     * @param requestDTO 绑定请求参数
     * @return 绑定结果
     */
    Response<UsagePartVO> bindPart(UsageBindPartRequestDTO requestDTO);

    /**
     * 解绑备件
     *
     * @param requestDTO 解绑请求参数
     * @return 是否解绑成功
     */
    Response<Boolean> unbindPart(UnBindPartRequestDTO requestDTO);

    /**
     * 清空用法关联的所有备件
     *
     * @param requestDTO 清空请求参数
     * @return 是否清空成功
     */
    Response<Boolean> clearParts(ClearPartsRequestDTO requestDTO);

    /**
     * 查询用法关联的所有备件
     *
     * @param usageId 用法ID
     * @return 备件列表
     */
    Response<List<UsagePartVO>> getPartsByUsageId(Long usageId);

    /**
     * 批量上传备件
     *
     * @param usageId 用法ID
     * @param file    Excel文件
     * @return 上传结果
     */
    Response<List<UsageBindPartResultVO>> batchUpload(Long usageId, MultipartFile file);

    /**
     * 上传模板
     *
     * @param file 模板文件
     * @return 上传结果信息
     */
    Response<String> uploadTemplate(MultipartFile file);

    /**
     * 获取模板信息
     *
     * @return 模板信息
     */
    Response<String> getTemplateInfo();

    /**
     * 下载模板
     *
     * @return 模板文件
     */
    ResponseEntity<byte[]> downloadTemplate();

}
