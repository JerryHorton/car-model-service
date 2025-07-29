package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IStructureTemplateService;
import cn.cug.sxy.api.dto.TemplateCreateRequestDTO;
import cn.cug.sxy.api.dto.TemplateQueryRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.NodeTreeVO;
import cn.cug.sxy.api.vo.TemplateBasePageVO;
import cn.cug.sxy.api.vo.TemplateBaseVO;
import cn.cug.sxy.api.vo.TemplateDetailVO;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;
import cn.cug.sxy.domain.structure.service.ITemplateService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/29 11:19
 * @Description
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/structure/template/")
public class StructureTemplateController implements IStructureTemplateService {

    private final ITemplateService templateService;

    public StructureTemplateController(ITemplateService templateService) {
        this.templateService = templateService;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @Override
    public Response<TemplateDetailVO> createTemplate(@RequestBody @Valid TemplateCreateRequestDTO requestDTO) {
        String templateCode = requestDTO.getTemplateCode();
        String templateName = requestDTO.getTemplateName();
        String templateDesc = requestDTO.getTemplateDesc();
        String version = requestDTO.getVersion();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建车型结构树模版 templateCode={}, templateName={}, templateDesc={}, version={}, creator={}",
                    templateCode, templateName, templateDesc, version, creator);
            // 1.创建模版
            StructureTemplateEntity template = templateService.createTemplate(
                    new TemplateCode(templateCode),
                    templateName,
                    templateDesc,
                    version,
                    creator
            );
            // 2. 批量创建节点
            if (requestDTO.getNodes() != null && !requestDTO.getNodes().isEmpty()) {
                createNodeTree(template.getId(), null, requestDTO.getNodes(), creator);
            }
            // 3. 查询创建的完整树并返回
            TemplateDetailVO templateDetail = getTemplateDetail(template.getId());
            log.info("创建车型结构树模版成功 templateId={}, templateCode={}, templateName={}, templateDesc={}, version={}, creator={}",
                    template.getId().getId(), templateCode, templateName, templateDesc, version, creator);

            return Response.<TemplateDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateDetail)
                    .build();
        } catch (AppException e) {
            log.error("创建车型结构树模版失败 templateCode={}, templateName={}, templateDesc={}, version={}, creator={}",
                    templateCode, templateName, templateDesc, version, creator, e);

            return Response.<TemplateDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建车型结构树模版异常 templateCode={}, templateName={}, templateDesc={}, version={}, creator={}",
                    templateCode, templateName, templateDesc, version, creator, e);

            return Response.<TemplateDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_template_detail", method = RequestMethod.GET)
    @Override
    public Response<TemplateDetailVO> getTemplateDetail(@RequestParam Long templateId) {
        try {
            if (templateId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            log.info("获取车型结构树模版详情 templateId={}", templateId);
            TemplateDetailVO templateDetail = getTemplateDetail(new TemplateId(templateId));
            log.info("获取车型结构树模版详情成功 templateId={}", templateId);
            return Response.<TemplateDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateDetail)
                    .build();
        } catch (AppException e) {
            log.error("获取车型结构树模版详情失败 templateId={}", templateId, e);
            return Response.<TemplateDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("获取车型结构树模版详情异常 templateId={}", templateId, e);
            return Response.<TemplateDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_templates", method = RequestMethod.POST)
    @Override
    public Response<TemplateBasePageVO> queryTemplates(@RequestBody TemplateQueryRequestDTO requestDTO) {
        String templateCode = requestDTO.getTemplateCode();
        String status = requestDTO.getStatus();
        String nameKeyword = requestDTO.getNameKeyword();
        Integer pageNo = requestDTO.getPageNo();
        Integer pageSize = requestDTO.getPageSize();
        try {
            log.info("查询车型结构树模版列表 templateCode={}, status={}, nameKeyword={}, pageNo={}, pageSize={}",
                    templateCode, status, nameKeyword, pageNo, pageSize);
            StructureTemplatePageVO templatePage = templateService.findTemplates(
                    StringUtils.isBlank(templateCode) ? null : new TemplateCode(templateCode),
                    StringUtils.isBlank(status) ? null : Status.fromCode(status),
                    nameKeyword,
                    pageNo,
                    pageSize
            );
            log.info("查询车型结构树模版列表成功 templateCode={}, status={}, pageNo={}, pageSize={}",
                    templateCode, status, pageNo, pageSize);

            return Response.<TemplateBasePageVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(TemplateBasePageVO.builder()
                            .pageNo(pageNo)
                            .pageSize(pageSize)
                            .totalPages(templatePage.getTotal())
                            .templates(templatePage.getTemplates().stream()
                                    .map(this::convertToTemplateBaseVO)
                                    .collect(Collectors.toList()))
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("查询车型结构树模版列表失败 templateCode={}, status={}, pageNo={}, pageSize={}",
                    templateCode, status, pageNo, pageSize, e);

            return Response.<TemplateBasePageVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询车型结构树模版列表异常 templateCode={}, status={}, pageNo={}, pageSize={}",
                    templateCode, status, pageNo, pageSize, e);

            return Response.<TemplateBasePageVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 获取模板详情VO
     */
    private TemplateDetailVO getTemplateDetail(TemplateId templateId) {
        Map<String, Object> templateData = templateService.getTemplateWithFullTree(templateId);
        StructureTemplateEntity template = (StructureTemplateEntity) templateData.get("template");
        List<StructureTemplateNodeEntity> nodes = (List<StructureTemplateNodeEntity>) templateData.get("nodes");
        // 转换为树形结构
        List<NodeTreeVO> nodeTree = buildNodeTree(nodes);
        // 构建返回VO
        return TemplateDetailVO.builder()
                .id(template.getId().getId())
                .templateCode(template.getTemplateCode().getCode())
                .templateName(template.getTemplateName())
                .version(template.getVersion())
                .status(template.getStatus().name())
                .creator(template.getCreator())
                .createdTime(template.getCreatedTime())
                .updatedTime(template.getUpdatedTime())
                .nodeTree(nodeTree)
                .build();
    }

    /**
     * 递归创建节点树
     */
    private void createNodeTree(TemplateId templateId, TemplateNodeId parentNodeId,
                                List<TemplateCreateRequestDTO.NodeCreate> nodes, String creator) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (TemplateCreateRequestDTO.NodeCreate nodeDTO : nodes) {
            // 创建当前节点
            StructureTemplateNodeEntity node = templateService.addNode(
                    templateId,
                    parentNodeId,
                    nodeDTO.getNodeName(),
                    nodeDTO.getNodeNameEn(),
                    nodeDTO.getNodeType(),
                    nodeDTO.getCategoryId(),
                    nodeDTO.getGroupId(),
                    nodeDTO.getSortOrder(),
                    creator
            );
            // 递归处理子节点
            createNodeTree(templateId, node.getId(), nodeDTO.getChildren(), creator);
        }
    }

    /**
     * 构建树形结构
     */
    private List<NodeTreeVO> buildNodeTree(List<StructureTemplateNodeEntity> nodes) {
        // 先构建节点映射
        Map<TemplateNodeId, NodeTreeVO> nodeMap = new HashMap<>();
        for (StructureTemplateNodeEntity node : nodes) {
            nodeMap.put(node.getId(), convertToNodeTreeVO(node));
        }
        // 构建树形结构
        List<NodeTreeVO> rootNodes = new ArrayList<>();
        for (StructureTemplateNodeEntity node : nodes) {
            NodeTreeVO nodeVO = nodeMap.get(node.getId());
            if (node.getParentId() == null) {
                // 根节点
                rootNodes.add(nodeVO);
            } else {
                // 子节点，添加到父节点的children列表
                NodeTreeVO parentNodeVO = nodeMap.get(node.getParentId());
                if (parentNodeVO != null) {
                    if (parentNodeVO.getChildren() == null) {
                        parentNodeVO.setChildren(new ArrayList<>());
                    }
                    parentNodeVO.getChildren().add(nodeVO);
                }
            }
        }

        return rootNodes;
    }

    /**
     * 转换为节点树VO
     */
    private NodeTreeVO convertToNodeTreeVO(StructureTemplateNodeEntity node) {
        return NodeTreeVO.builder()
                .id(node.getId().getId())
                .nodeCode(node.getNodeCode())
                .nodeName(node.getNodeName())
                .nodeNameEn(node.getNodeNameEn())
                .nodeType(node.getNodeType().name())
                .sortOrder(node.getSortOrder())
                .parentId(node.getParentId() != null ? node.getParentId().getId() : null)
                .children(new ArrayList<>())
                .build();
    }

    /**
     * 转换为模板基本VO
     */
    private TemplateBaseVO convertToTemplateBaseVO(StructureTemplateEntity template) {
        return TemplateBaseVO.builder()
                .id(template.getId().getId())
                .templateCode(template.getTemplateCode().getCode())
                .templateName(template.getTemplateName())
                .version(template.getVersion())
                .status(template.getStatus().name())
                .creator(template.getCreator())
                .createdTime(template.getCreatedTime())
                .updatedTime(template.getUpdatedTime())
                .build();
    }

}
