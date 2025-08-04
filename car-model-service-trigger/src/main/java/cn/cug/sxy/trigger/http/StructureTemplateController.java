package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IStructureTemplateService;
import cn.cug.sxy.api.dto.*;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.*;
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
 * @Description 车型结构树模版服务
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
            log.info("查询车型结构树模版列表成功 templateCode={}, status={}, pageNo={}, pageSize={}, total={}",
                    templateCode, status, pageNo, pageSize, templatePage.getTotal());

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

    @RequestMapping(value = "validate_structure", method = RequestMethod.POST)
    @Override
    public Response<TemplateValidateResultVO> validateStructure(@RequestParam Long templateId) {
        try {
            log.info("校验车型结构树模版 templateId={}", templateId);
            if (templateId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            Map<String, Object> validateResult = templateService.validateTemplateStructure(new TemplateId(templateId));
            boolean valid = (boolean) validateResult.get("valid");
            @SuppressWarnings("unchecked")
            List<String> issues = (List<String>) validateResult.get("issues");
            TemplateValidateResultVO resultVO = TemplateValidateResultVO.builder()
                    .valid(valid)
                    .issues(issues)
                    .build();
            log.info("校验车型结构树模版成功 templateId={}, valid={}", templateId, valid);

            return Response.<TemplateValidateResultVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(resultVO)
                    .build();
        } catch (AppException e) {
            log.error("校验车型结构树模版失败 templateId={}", templateId, e);
            return Response.<TemplateValidateResultVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("校验车型结构树模版异常 templateId={}", templateId, e);
            return Response.<TemplateValidateResultVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "create_new_version", method = RequestMethod.POST)
    @Override
    public Response<TemplateDetailVO> createNewVersion(@RequestBody @Valid TemplateNewVersionRequestDTO requestDTO) {
        Long templateId = requestDTO.getTemplateId();
        String newVersion = requestDTO.getNewVersion();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建车型结构树模版新版本 templateId={}, newVersion={}, creator={}",
                    templateId, newVersion, creator);
            StructureTemplateEntity newTemplate = templateService.createNewVersionWithNodes(
                    new TemplateId(templateId),
                    requestDTO.getNewVersion(),
                    requestDTO.getCreator()
            );
            TemplateDetailVO templateDetail = getTemplateDetail(newTemplate.getId());
            log.info("创建车型结构树模版新版本成功 templateId={} newVersion={} creator={}",
                    templateId, newVersion, creator);

            return Response.<TemplateDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateDetail)
                    .build();
        } catch (AppException e) {
            log.error("创建车型结构树模版新版本失败 templateId={} newVersion={} creator={}",
                    templateId, newVersion, creator, e);

            return Response.<TemplateDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建车型结构树模版新版本异常 templateId={} newVersion={} creator={}",
                    templateId, newVersion, creator, e);

            return Response.<TemplateDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "add_node", method = RequestMethod.POST)
    @Override
    public Response<TemplateNodeVO> addNode(@RequestBody @Valid TemplateAddNodeRequestDTO requestDTO) {
        Long templateId = requestDTO.getTemplateId();
        String nodeName = requestDTO.getNodeName();
        try {
            log.info("添加车型结构树模版节点 templateId={}, nodeName={}", templateId, nodeName);
            StructureTemplateNodeEntity node = templateService.addNode(
                    new TemplateId(templateId),
                    requestDTO.getParentNodeId() == null ? null : new TemplateNodeId(requestDTO.getParentNodeId()),
                    requestDTO.getNodeName(),
                    requestDTO.getNodeNameEn(),
                    requestDTO.getNodeType(),
                    requestDTO.getCategoryId(),
                    requestDTO.getGroupId(),
                    requestDTO.getSortOrder(),
                    requestDTO.getCreator()
            );
            log.info("添加车型结构树模版节点成功 templateId={}, nodeName={}", templateId, nodeName);
            TemplateNodeVO templateNodeVO = convertToNodeVO(node);

            return Response.<TemplateNodeVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateNodeVO)
                    .build();
        } catch (AppException e) {
            log.error("添加车型结构树模版节点失败 templateId={}, nodeName={}", templateId, nodeName, e);
            return Response.<TemplateNodeVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("添加车型结构树模版节点异常 templateId={}, nodeName={}", templateId, nodeName, e);
            return Response.<TemplateNodeVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "move_node", method = RequestMethod.POST)
    @Override
    public Response<Boolean> moveNode(@RequestBody @Valid TemplateMoveNodeRequestDTO requestDTO) {
        Long nodeId = requestDTO.getNodeId();
        Long newParentId = requestDTO.getNewParentId();
        Integer sortOrder = requestDTO.getSortOrder();
        try {
            log.info("移动车型结构树模版节点 nodeId={}, newParentId={}, sortOrder={}", nodeId, newParentId, sortOrder);
            boolean success = templateService.moveNode(
                    new TemplateNodeId(nodeId),
                    newParentId == null ? null : new TemplateNodeId(newParentId),
                    sortOrder
            );
            log.info("移动车型结构树模版节点成功 nodeId={}, newParentId={}, sortOrder={}", nodeId, newParentId, sortOrder);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(success)
                    .build();
        } catch (AppException e) {
            log.error("移动车型结构树模版节点失败 nodeId={}, newParentId={}, sortOrder={}", nodeId, newParentId, sortOrder, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("移动车型结构树模版节点异常 nodeId={}, newParentId={}, sortOrder={}", nodeId, newParentId, sortOrder, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_node", method = RequestMethod.POST)
    @Override
    public Response<Integer> deleteNode(@RequestParam Long nodeId) {
        try {
            log.info("删除车型结构树模版节点 nodeId={}", nodeId);
            int deletedCount = templateService.deleteNodeAndChildren(new TemplateNodeId(nodeId));
            log.info("删除车型结构树模版节点成功 nodeId={}, deletedCount={}", nodeId, deletedCount);

            return Response.<Integer>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(deletedCount)
                    .build();
        } catch (AppException e) {
            log.error("删除车型结构树模版节点失败 nodeId={}", nodeId, e);

            return Response.<Integer>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除车型结构树模版节点异常 nodeId={}", nodeId, e);

            return Response.<Integer>builder()
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
        @SuppressWarnings("unchecked")
        List<StructureTemplateNodeEntity> nodes = (List<StructureTemplateNodeEntity>) templateData.get("nodes");
        // 转换为树形结构
        List<TemplateNodeTreeVO> nodeTree = buildNodeTree(nodes);
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
    private List<TemplateNodeTreeVO> buildNodeTree(List<StructureTemplateNodeEntity> nodes) {
        // 先构建节点映射
        Map<TemplateNodeId, TemplateNodeTreeVO> nodeMap = new HashMap<>();
        for (StructureTemplateNodeEntity node : nodes) {
            nodeMap.put(node.getId(), convertToNodeTreeVO(node));
        }
        // 构建树形结构
        List<TemplateNodeTreeVO> rootNodes = new ArrayList<>();
        for (StructureTemplateNodeEntity node : nodes) {
            TemplateNodeTreeVO nodeVO = nodeMap.get(node.getId());
            if (node.getParentId() == null) {
                // 根节点
                rootNodes.add(nodeVO);
            } else {
                // 子节点，添加到父节点的children列表
                TemplateNodeTreeVO parentNodeVO = nodeMap.get(node.getParentId());
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
    private TemplateNodeTreeVO convertToNodeTreeVO(StructureTemplateNodeEntity node) {
        return TemplateNodeTreeVO.builder()
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

    /**
     * 转换为节点VO
     */
    private TemplateNodeVO convertToNodeVO(StructureTemplateNodeEntity node) {
        return TemplateNodeVO.builder()
                .id(node.getId().getId())
                .templateId(node.getTemplateId().getId())
                .parentId(node.getParentId() != null ? node.getParentId().getId() : null)
                .nodeCode(node.getNodeCode())
                .nodeName(node.getNodeName())
                .nodeNameEn(node.getNodeNameEn())
                .nodeType(node.getNodeType().name())
                .sortOrder(node.getSortOrder())
                .status(node.getStatus().name())
                .creator(node.getCreator())
                .createdTime(node.getCreatedTime())
                .updatedTime(node.getUpdatedTime())
                .build();
    }

}
