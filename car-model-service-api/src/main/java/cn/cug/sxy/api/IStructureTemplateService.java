package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.TemplateCreateRequestDTO;
import cn.cug.sxy.api.dto.TemplateQueryRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.TemplateBasePageVO;
import cn.cug.sxy.api.vo.TemplateBaseVO;
import cn.cug.sxy.api.vo.TemplateDetailVO;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 10:50
 * @Description 结构树模板服务接口
 * @Author jerryhotton
 */

public interface IStructureTemplateService {

    /**
     * 创建结构树模板
     *
     * @param requestDTO 创建结构树模板请求参数
     * @return 结构树模板详情
     */
    Response<TemplateDetailVO> createTemplate(TemplateCreateRequestDTO requestDTO);

    /**
     * 获取模板详情
     *
     * @param templateId 模版ID
     * @return 结构树模板详情
     */
    Response<TemplateDetailVO> getTemplateDetail(Long templateId);

    /**
     * 查询模板列表
     *
     * @param requestDTO 模版查询请求DTO
     * @return 模板列表
     */
    Response<TemplateBasePageVO> queryTemplates(TemplateQueryRequestDTO requestDTO);

}
