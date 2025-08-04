package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.*;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.TemplateNodeVO;
import cn.cug.sxy.api.vo.TemplateBasePageVO;
import cn.cug.sxy.api.vo.TemplateDetailVO;
import cn.cug.sxy.api.vo.TemplateValidateResultVO;

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

    /**
     * 校验结构树模板
     *
     * @param templateId 模板ID
     * @return 校验结果
     */
    Response<TemplateValidateResultVO> validateStructure(Long templateId);

    /**
     * 创建模板新版本
     *
     * @param requestDTO 新版本请求DTO
     * @return 新版本详情
     */
    Response<TemplateDetailVO> createNewVersion(TemplateNewVersionRequestDTO requestDTO);

    /**
     * 添加节点
     *
     * @param requestDTO 添加节点请求DTO
     * @return 节点VO
     */
    Response<TemplateNodeVO> addNode(TemplateAddNodeRequestDTO requestDTO);

    /**
     * 移动节点
     *
     * @param requestDTO 移动节点请求DTO
     * @return 移动结果
     */
    Response<Boolean> moveNode(TemplateMoveNodeRequestDTO requestDTO);

    /**
     * 删除节点
     *
     * @param nodeId 节点ID
     * @return 删除的节点数量
     */
    Response<Integer> deleteNode(Long nodeId);

}
