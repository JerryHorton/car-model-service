package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.ChangePartStatusRequestDTO;
import cn.cug.sxy.api.dto.PartCreateRequestDTO;
import cn.cug.sxy.api.dto.PartUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.PartBindHourResultVO;
import cn.cug.sxy.api.vo.PartDetailVO;
import cn.cug.sxy.api.vo.PartVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件服务接口
 * @Author jerryhotton
 */
public interface IPartService {

    /**
     * 创建备件
     *
     * @param requestDTO 创建请求DTO
     * @return 创建结果
     */
    Response<PartVO> createPart(PartCreateRequestDTO requestDTO);

    /**
     * 上传备件工时关联模板
     *
     * @param file 模板文件
     * @return 上传结果
     */
    Response<String> uploadTemplate(MultipartFile file);

    /**
     * 下载备件工时关联模板
     *
     * @return 模板文件
     */
    ResponseEntity<byte[]> downloadTemplate();

    /**
     * 获取备件工时关联模板信息
     *
     * @return 模板信息
     */
    Response<String> getTemplateInfo();

    /**
     * 更新备件
     *
     * @param requestDTO 更新请求DTO
     * @return 更新结果
     */
    Response<PartVO> updatePart(PartUpdateRequestDTO requestDTO);

    /**
     * 查询备件详情
     *
     * @param partId 备件ID
     * @return 备件详情
     */
    Response<PartDetailVO> getPartDetail(Long partId);

    /**
     * 查询所有备件
     *
     * @return 备件列表
     */
    Response<List<PartVO>> listParts();

    /**
     * 启用备件
     *
     * @param requestDTO 启用请求DTO
     * @return 操作结果
     */
    Response<Boolean> enablePart(ChangePartStatusRequestDTO requestDTO);

    /**
     * 禁用备件
     *
     * @param requestDTO 禁用请求DTO
     * @return 操作结果
     */
    Response<Boolean> disablePart(ChangePartStatusRequestDTO requestDTO);

    /**
     * 删除备件
     *
     * @param requestDTO 删除请求DTO
     * @return 操作结果
     */
    Response<Boolean> deletePart(ChangePartStatusRequestDTO requestDTO);

    /**
     * 批量绑定备件工时关系
     *
     * @param file    Excel文件，包含备件编码和工时代码
     * @param creator 创建人
     * @return 批量绑定结果
     */
    Response<List<PartBindHourResultVO>> batchBindHours(MultipartFile file, String creator);

    /**
     * 解绑工时
     *
     * @param partId 备件ID
     * @param hourId 工时ID
     * @return 操作结果
     */
    Response<Boolean> unbindHour(Long partId, Long hourId);

}