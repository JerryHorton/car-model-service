package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.WorkHourCreateRequestDTO;
import cn.cug.sxy.api.dto.WorkHourUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.WorkHourBatchUploadResultVO;
import cn.cug.sxy.api.vo.WorkHourTreeVO;
import cn.cug.sxy.api.vo.WorkHourVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时服务接口
 * @Author jerryhotton
 */

public interface IWorkHourService {

    /**
     * 创建主工时
     *
     * @param requestDTO 创建请求DTO
     * @return 创建结果
     */
    Response<WorkHourVO> createMainWorkHour(WorkHourCreateRequestDTO requestDTO);

    /**
     * 创建子工时
     *
     * @param requestDTO 创建请求DTO
     * @return 创建结果
     */
    Response<WorkHourVO> createSubWorkHour(WorkHourCreateRequestDTO requestDTO);

    /**
     * 批量上传子工时
     *
     * @param file     Excel文件
     * @param parentId 父工时ID
     * @param creator  创建人
     * @return 批量上传结果列表
     */
    Response<List<WorkHourBatchUploadResultVO>> batchUploadSubWorkHours(MultipartFile file, Long parentId, String creator);

    /**
     * 上传工时批量上传模板
     *
     * @param file Excel模板文件
     * @return 上传结果信息
     */
    Response<String> uploadTemplate(MultipartFile file);

    /**
     * 下载工时批量上传模板
     *
     * @return 模板文件字节数组
     */
    ResponseEntity<byte[]> downloadTemplate();

    /**
     * 获取工时批量上传模板信息
     *
     * @return 模板文件信息
     */
    Response<String> getTemplateInfo();

    /**
     * 更新工时
     *
     * @param requestDTO 更新请求DTO
     * @return 更新结果
     */
    Response<WorkHourVO> updateWorkHour(WorkHourUpdateRequestDTO requestDTO);

    /**
     * 查询所有主工时
     *
     * @return 主工时列表
     */
    Response<List<WorkHourVO>> queryAllMainWorkHours();

    /**
     * 根据父ID查询子工时
     *
     * @param parentId 父工时ID
     * @return 子工时列表
     */
    Response<List<WorkHourVO>> querySubWorkHoursByParentId(Long parentId);

    /**
     * 查询工时树结构
     *
     * @param workHourId 工时ID
     * @return 工时树结构
     */
    Response<WorkHourTreeVO> queryWorkHourTree(Long workHourId);

    /**
     * 根据ID查询工时详情
     *
     * @param workHourId 工时ID
     * @return 工时详情
     */
    Response<WorkHourVO> getWorkHourDetail(Long workHourId);

    /**
     * 启用工时
     *
     * @param workHourId 工时ID
     * @return 操作结果
     */
    Response<Boolean> enableWorkHour(Long workHourId);

    /**
     * 禁用工时
     *
     * @param workHourId 工时ID
     * @return 操作结果
     */
    Response<Boolean> disableWorkHour(Long workHourId);

    /**
     * 删除工时
     *
     * @param workHourId 工时ID
     * @return 操作结果
     */
    Response<Boolean> deleteWorkHour(Long workHourId);

} 