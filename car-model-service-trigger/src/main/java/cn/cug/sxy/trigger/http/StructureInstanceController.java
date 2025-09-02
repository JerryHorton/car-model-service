package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IStructureInstanceService;
import cn.cug.sxy.api.dto.*;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.*;
import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;
import cn.cug.sxy.domain.structure.service.IInstanceService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/31 16:31
 * @Description 车型结构树实例服务
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/structure/instance/")
@DubboService(version = "1.0")
public class StructureInstanceController implements IStructureInstanceService {

    private final IInstanceService instanceService;

    public StructureInstanceController(IInstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @RequestMapping(value = "create_instance", method = RequestMethod.POST)
    @Override
    public Response<InstanceDetailVO> createInstance(@RequestBody @Valid InstanceCreateRequestDTO requestDTO) {
        String instanceCode = requestDTO.getInstanceCode();
        String instanceName = requestDTO.getInstanceName();
        String instanceDesc = requestDTO.getInstanceDesc();
        Long seriesId = requestDTO.getSeriesId();
        Long modelId = requestDTO.getModelId();
        Long templateId = requestDTO.getTemplateId();
        String version = requestDTO.getVersion();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建车型结构树实例 instanceCode={}, instanceName={}, instanceDesc={}, seriesId={}, modelId={}, templateId={}, version={}, creator={}",
                    instanceCode, instanceName, instanceDesc, seriesId, modelId, templateId, version, creator);
            // 1.创建实例
            StructureInstanceEntity instance = instanceService.createInstanceFromTemplate(
                    new InstanceCode(instanceCode),
                    instanceName,
                    instanceDesc,
                    new SeriesId(seriesId),
                    new ModelId(modelId),
                    new TemplateId(templateId),
                    version,
                    creator
            );
            // 2. 查询创建的完整树并返回
            InstanceDetailVO detailVO = getInstanceDetailVO(instance.getId());
            log.info("创建车型结构树实例成功 instanceCode={}, instanceName={}, instanceDesc={}, seriesId={}, modelId={}, templateId={}, version={}, creator={}",
                    instanceCode, instanceName, instanceDesc, seriesId, modelId, templateId, version, creator);

            return Response.<InstanceDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(detailVO)
                    .build();
        } catch (AppException e) {
            log.error("创建车型结构树实例失败 instanceCode={}, instanceName={}, instanceDesc={}, seriesId={}, modelId={}, templateId={}, version={}, creator={}",
                    instanceCode, instanceName, instanceDesc, seriesId, modelId, templateId, version, creator, e);

            return Response.<InstanceDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建车型结构树实例异常 instanceCode={}, instanceName={}, instanceDesc={}, seriesId={}, modelId={}, templateId={}, version={}, creator={}",
                    instanceCode, instanceName, instanceDesc, seriesId, modelId, templateId, version, creator, e);

            return Response.<InstanceDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_instance_detail", method = RequestMethod.GET)
    @Override
    public Response<InstanceDetailVO> getInstanceDetail(@RequestParam Long instanceId) {
        try {
            log.info("获取车型结构树实例详情 instanceId={}", instanceId);
            InstanceDetailVO detailVO = getInstanceDetailVO(new InstanceId(instanceId));
            log.info("获取车型结构树实例详情成功 instanceId={}", instanceId);

            return Response.<InstanceDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(detailVO)
                    .build();
        } catch (AppException e) {
            log.error("获取车型结构树实例详情失败 instanceId={}", instanceId, e);

            return Response.<InstanceDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("获取车型结构树实例详情异常 instanceId={}", instanceId, e);

            return Response.<InstanceDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_instances", method = RequestMethod.POST)
    @Override
    public Response<InstanceBasePageVO> queryInstances(@RequestBody InstanceQueryRequestDTO requestDTO) {
        String instanceCode = requestDTO.getInstanceCode();
        String nameKeyword = requestDTO.getNameKeyword();
        String status = requestDTO.getStatus();
        Long seriesId = requestDTO.getSeriesId();
        Long modelId = requestDTO.getModelId();
        Integer pageNo = requestDTO.getPageNo();
        Integer pageSize = requestDTO.getPageSize();
        try {
            log.info("查询车型结构树实例列表 instanceCode={}, nameKeyword={}, status={}, seriesId={}, modelId={}, pageNo={}, pageSize={}",
                    instanceCode, nameKeyword, status, seriesId, modelId, pageNo, pageSize);
            StructureInstancePageVO instancePage = instanceService.findInstances(
                    StringUtils.isBlank(instanceCode) ? null : new InstanceCode(instanceCode),
                    StringUtils.isBlank(nameKeyword) ? null : nameKeyword,
                    StringUtils.isBlank(status) ? null : Status.fromCode(status),
                    seriesId == null ? null : new SeriesId(seriesId),
                    modelId == null ? null : new ModelId(modelId),
                    pageNo,
                    pageSize);
            log.info("查询车型结构树实例列表成功 instanceCode={}, nameKeyword={}, status={}, seriesId={}, modelId={}, pageNo={}, pageSize={},total={}",
                    instanceCode, nameKeyword, status, seriesId, modelId, pageNo, pageSize, instancePage.getTotal());

            return Response.<InstanceBasePageVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(InstanceBasePageVO.builder()
                            .pageNo(instancePage.getCurrentPage())
                            .pageSize(pageSize)
                            .total(instancePage.getTotal())
                            .totalPages(instancePage.getTotalPages())
                            .instances(instancePage.getInstances().stream()
                                    .map(this::convertToInstanceBaseVO)
                                    .collect(Collectors.toList()))
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("查询车型结构树实例列表失败 instanceCode={}, nameKeyword={}, status={}, seriesId={}, modelId={}, pageNo={}, pageSize={}",
                    instanceCode, nameKeyword, status, seriesId, modelId, pageNo, pageSize, e);

            return Response.<InstanceBasePageVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询车型结构树实例列表异常 instanceCode={}, nameKeyword={}, status={}, seriesId={}, modelId={}, pageNo={}, pageSize={}",
                    instanceCode, nameKeyword, status, seriesId, modelId, pageNo, pageSize, e);

            return Response.<InstanceBasePageVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "create_new_version", method = RequestMethod.POST)
    @Override
    public Response<InstanceDetailVO> createNewVersion(@RequestBody @Valid InstanceNewVersionRequestDTO requestDTO) {
        Long instanceId = requestDTO.getInstanceId();
        String newVersion = requestDTO.getNewVersion();
        String instanceDesc = requestDTO.getInstanceDesc();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建车型结构树实例新版本 instanceId={}, newVersion={}, instanceDesc={}, creator={}",
                    instanceId, newVersion, instanceDesc, creator);
            StructureInstanceEntity newInstance = instanceService.createNewVersionWithNodes(
                    new InstanceId(instanceId),
                    newVersion,
                    instanceDesc,
                    creator);
            InstanceDetailVO instanceDetailVO = getInstanceDetailVO(newInstance.getId());
            log.info("创建车型结构树实例新版本成功 instanceId={}, newVersion={}, instanceDesc={}, creator={}",
                    instanceId, newVersion, instanceDesc, creator);
            return Response.<InstanceDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(instanceDetailVO)
                    .build();
        } catch (AppException e) {
            log.error("创建车型结构树实例新版本失败 instanceId={}, newVersion={}, instanceDesc={}, creator={}",
                    instanceId, newVersion, instanceDesc, creator, e);

            return Response.<InstanceDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建车型结构树实例新版本异常 instanceId={}, newVersion={}, instanceDesc={}, creator={}",
                    instanceId, newVersion, instanceDesc, creator, e);

            return Response.<InstanceDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "add_node", method = RequestMethod.POST)
    @Override
    public Response<InstanceNodeVO> addNode(@RequestBody @Valid InstanceAddNodeRequestDTO requestDTO) {
        Long instanceId = requestDTO.getInstanceId();
        String nodeName = requestDTO.getNodeName();
        try {
            log.info("添加车型结构树实例节点 instanceId={}, nodeName={}", instanceId, nodeName);
            StructureInstanceNodeEntity node = instanceService.addNode(
                    new InstanceId(instanceId),
                    requestDTO.getParentNodeId() == null ? null : new InstanceNodeId(requestDTO.getParentNodeId()),
                    requestDTO.getNodeName(),
                    requestDTO.getNodeNameEn(),
                    requestDTO.getNodeType(),
                    requestDTO.getCategoryId(),
                    requestDTO.getGroupId(),
                    requestDTO.getUsageId(),
                    requestDTO.getSortOrder(),
                    requestDTO.getCreator()
            );
            log.info("添加车型结构树实例节点成功 instanceId={}, nodeName={}", instanceId, nodeName);
            InstanceNodeVO instanceNodeVO = convertToNodeVO(node);

            return Response.<InstanceNodeVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(instanceNodeVO)
                    .build();
        } catch (AppException e) {
            log.error("添加车型结构树实例节点失败 instanceId={}, nodeName={}", instanceId, nodeName, e);

            return Response.<InstanceNodeVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("添加车型结构树实例节点异常 instanceId={}, nodeName={}", instanceId, nodeName, e);

            return Response.<InstanceNodeVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }

    @RequestMapping(value = "move_node", method = RequestMethod.POST)
    @Override
    public Response<Boolean> moveNode(@RequestBody @Valid InstanceMoveNodeRequestDTO requestDTO) {
        Long nodeId = requestDTO.getNodeId();
        Long newParentId = requestDTO.getNewParentId();
        Integer sortOrder = requestDTO.getSortOrder();
        try {
            log.info("移动车型结构树实例节点 nodeId={}, newParentId={}, sortOrder={}",
                    requestDTO.getNodeId(), newParentId, sortOrder);
            boolean success = instanceService.moveNode(
                    new InstanceNodeId(nodeId),
                    newParentId == null ? null : new InstanceNodeId(newParentId),
                    sortOrder
            );
            log.info("移动车型结构树实例节点成功 nodeId={}, newParentId={}, sortOrder={}, success={}",
                    nodeId, newParentId, sortOrder, success);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(success)
                    .build();
        } catch (AppException e) {
            log.error("移动车型结构树实例节点失败 nodeId={}, newParentId={}, sortOrder={}",
                    nodeId, newParentId, sortOrder, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(false)
                    .build();
        } catch (Exception e) {
            log.error("移动车型结构树实例节点异常 nodeId={}, newParentId={}, sortOrder={}",
                    nodeId, newParentId, sortOrder, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @RequestMapping(value = "query_child_node", method = RequestMethod.GET)
    @Override
    public Response<List<InstanceNodeVO>> queryChildNode(@RequestParam Long parentId) {
        try {
            log.info("查询车型结构树实例节点子节点 parentId={}", parentId);
            if (parentId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            List<StructureInstanceNodeEntity> childNodes = instanceService.findChildNodes(new InstanceNodeId(parentId));
            List<InstanceNodeVO> instanceNodeVOS = childNodes.stream().map(this::convertToNodeVO).toList();
            log.info("查询车型结构树实例节点子节点成功 parentId={}", parentId);
            return Response.<List<InstanceNodeVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(instanceNodeVOS)
                    .build();
        } catch (AppException e) {
            log.error("查询车型结构树实例节点子节点失败 parentId={}", parentId, e);

            return Response.<List<InstanceNodeVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询车型结构树实例节点子节点异常 parentId={}", parentId, e);

            return Response.<List<InstanceNodeVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_node", method = RequestMethod.POST)
    @Override
    public Response<Integer> deleteNode(@RequestParam Long nodeId) {
        try {
            log.info("删除车型结构树实例节点 nodeId={}", nodeId);
            if (nodeId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            int count = instanceService.deleteNodeAndChildren(new InstanceNodeId(nodeId));
            log.info("删除车型结构树实例节点成功 nodeId={}, count={}", nodeId, count);

            return Response.<Integer>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(count)
                    .build();
        } catch (AppException e) {
            log.error("删除车型结构树实例节点失败 nodeId={}", nodeId, e);

            return Response.<Integer>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除车型结构树实例节点异常 nodeId={}", nodeId, e);

            return Response.<Integer>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "validate_structure", method = RequestMethod.POST)
    @Override
    public Response<InstanceValidateResultVO> validateStructure(@RequestParam Long instanceId) {
        try {
            log.info("校验车型结构树实例结构 instanceId={}", instanceId);
            if (instanceId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            Map<String, Object> validateResult = instanceService.validateInstanceStructure(new InstanceId(instanceId));
            boolean valid = (boolean) validateResult.get("valid");
            @SuppressWarnings("unchecked")
            List<String> issues = (List<String>) validateResult.get("issues");
            InstanceValidateResultVO validateResultVO = InstanceValidateResultVO.builder()
                    .valid(valid)
                    .issues(issues)
                    .build();
            log.info("校验车型结构树实例结构成功 instanceId={} valid={}", instanceId, valid);

            return Response.<InstanceValidateResultVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(validateResultVO)
                    .build();
        } catch (AppException e) {
            log.error("校验车型结构树实例结构失败 instanceId={}", instanceId, e);

            return Response.<InstanceValidateResultVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("校验车型结构树实例结构异常 instanceId={}", instanceId, e);

            return Response.<InstanceValidateResultVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "publish_instance", method = RequestMethod.POST)
    @Override
    public Response<Boolean> publishInstance(@RequestBody @Valid InstancePublishRequestDTO requestDTO) {
        Long instanceId = requestDTO.getInstanceId();
        LocalDateTime effectiveTime = requestDTO.getEffectiveTime();
        try {
            log.info("发布车型结构树实例 instanceId={}, effectiveTime={}", instanceId, effectiveTime);
            boolean success = instanceService.publishInstance(new InstanceId(instanceId), effectiveTime);
            log.info("发布车型结构树实例成功 instanceId={}, effectiveTime={}, success={}", instanceId, effectiveTime, success);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(success)
                    .build();
        } catch (AppException e) {
            log.error("发布车型结构树实例失败 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(false)
                    .build();
        } catch (Exception e) {
            log.error("发布车型结构树实例异常 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @RequestMapping(value = "unpublish_instance", method = RequestMethod.POST)
    @Override
    public Response<Boolean> unpublishInstance(@RequestParam Long instanceId) {
        try {
            if (instanceId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            log.info("取消发布车型结构树实例 instanceId={}", instanceId);
            boolean success = instanceService.unpublishInstance(new InstanceId(instanceId));
            log.info("取消发布车型结构树实例成功 instanceId={}, success={}", instanceId, success);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(success)
                    .build();
        } catch (AppException e) {
            log.error("取消发布车型结构树实例失败 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(false)
                    .build();
        } catch (Exception e) {
            log.error("取消发布车型结构树实例异常 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @RequestMapping(value = "enable_instance", method = RequestMethod.POST)
    @Override
    public Response<Boolean> enableInstance(@RequestParam Long instanceId) {
        try {
            if (instanceId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            log.info("启用车型结构树实例 instanceId={}", instanceId);
            boolean success = instanceService.enableInstance(new InstanceId(instanceId));
            log.info("启用车型结构树实例成功 instanceId={}, success={}", instanceId, success);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(success)
                    .build();
        } catch (AppException e) {
            log.error("启用车型结构树实例失败 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(false)
                    .build();
        } catch (Exception e) {
            log.error("启用车型结构树实例异常 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @RequestMapping(value = "disable_instance", method = RequestMethod.POST)
    @Override
    public Response<Boolean> disableInstance(@RequestParam Long instanceId) {
        try {
            if (instanceId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            log.info("禁用车型结构树实例 instanceId={}", instanceId);
            boolean success = instanceService.disableInstance(new InstanceId(instanceId));
            log.info("禁用车型结构树实例成功 instanceId={}, success={}", instanceId, success);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(success)
                    .build();
        } catch (AppException e) {
            log.error("禁用车型结构树实例失败 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(false)
                    .build();
        } catch (Exception e) {
            log.error("禁用车型结构树实例异常 instanceId={}", instanceId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @RequestMapping(value = "compare_instances", method = RequestMethod.POST)
    @Override
    public Response<InstanceCompareResultVO> compareInstances(@RequestParam Long instanceId1, @RequestParam Long instanceId2) {
        try {
            if (instanceId1 == null || instanceId2 == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            Map<String, Object> compareResult = instanceService.compareInstances(
                    new InstanceId(instanceId1),
                    new InstanceId(instanceId2)
            );
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> added = (List<Map<String, Object>>) compareResult.get("added");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> removed = (List<Map<String, Object>>) compareResult.get("removed");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> modified = (List<Map<String, Object>>) compareResult.get("modified");
            // 转换为VO
            List<InstanceNodeDiffVO> addedVOs = convertToDiffVOs(added, "added");
            List<InstanceNodeDiffVO> removedVOs = convertToDiffVOs(removed, "removed");
            List<InstanceNodeDiffVO> modifiedVOs = convertToDiffVOs(modified, "modified");
            InstanceCompareResultVO compareResultVO = InstanceCompareResultVO.builder()
                    .added(addedVOs)
                    .removed(removedVOs)
                    .modified(modifiedVOs)
                    .totalAdded(addedVOs.size())
                    .totalRemoved(removedVOs.size())
                    .totalModified(modifiedVOs.size())
                    .build();

            return Response.<InstanceCompareResultVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(compareResultVO)
                    .build();
        } catch (AppException e) {
            log.error("比较车型结构树实例失败 instanceId1={}, instanceId2={}", instanceId1, instanceId2, e);

            return Response.<InstanceCompareResultVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("比较车型结构树实例异常 instanceId1={}, instanceId2={}", instanceId1, instanceId2, e);

            return Response.<InstanceCompareResultVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(null)
                    .build();
        }
    }

    /**
     * 获取实例详情VO
     */
    private InstanceDetailVO getInstanceDetailVO(InstanceId instanceId) {
        Map<String, Object> instanceData = instanceService.getInstanceWithFullTree(instanceId);
        StructureInstanceEntity instance = (StructureInstanceEntity) instanceData.get("instance");
        @SuppressWarnings("unchecked")
        List<StructureInstanceNodeEntity> nodes = (List<StructureInstanceNodeEntity>) instanceData.get("nodes");
        // 转换为树形结构
        List<InstanceNodeTreeVO> nodeTree = buildNodeTree(nodes);

        // 构建返回VO
        return InstanceDetailVO.builder()
                .id(instance.getId().getId())
                .instanceCode(instance.getInstanceCode().getCode())
                .instanceName(instance.getInstanceName())
                .instanceDesc(instance.getInstanceDesc())
                .seriesId(instance.getSeriesId() != null ? instance.getSeriesId().getId() : null)
                .modelId(instance.getModelId() != null ? instance.getModelId().getId() : null)
                .instanceVersion(instance.getVersion())
                .status(instance.getStatus().name())
                .isPublished(instance.getIsPublished())
                .effectiveTime(instance.getEffectiveTime())
                .creator(instance.getCreator())
                .createdTime(instance.getCreatedTime())
                .updatedTime(instance.getUpdatedTime())
                .nodeTree(nodeTree)
                .build();
    }

    /**
     * 构建完整树形结构
     */
    private List<InstanceNodeTreeVO> buildNodeTree(List<StructureInstanceNodeEntity> nodes) {
        // 先构建节点映射
        Map<InstanceNodeId, InstanceNodeTreeVO> nodeMap = new HashMap<>();
        for (StructureInstanceNodeEntity node : nodes) {
            nodeMap.put(node.getId(), convertToNodeTreeVO(node));
        }
        // 构建树形结构
        List<InstanceNodeTreeVO> rootNodes = new ArrayList<>();
        for (StructureInstanceNodeEntity node : nodes) {
            InstanceNodeTreeVO nodeVO = nodeMap.get(node.getId());
            if (node.getParentId() == null) {
                // 根节点
                rootNodes.add(nodeVO);
            } else {
                // 子节点，添加到父节点的children列表
                InstanceNodeTreeVO parentNodeVO = nodeMap.get(node.getParentId());
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
     * 转换为实例基本VO
     */
    private InstanceBaseVO convertToInstanceBaseVO(StructureInstanceEntity instance) {
        return InstanceBaseVO.builder()
                .id(instance.getId().getId())
                .instanceCode(instance.getInstanceCode().getCode())
                .instanceName(instance.getInstanceName())
                .instanceDesc(instance.getInstanceDesc())
                .seriesId(instance.getSeriesId() != null ? instance.getSeriesId().getId() : null)
                .modelId(instance.getModelId() != null ? instance.getModelId().getId() : null)
                .instanceVersion(instance.getVersion())
                .status(instance.getStatus().name())
                .isPublished(instance.getIsPublished())
                .effectiveTime(instance.getEffectiveTime())
                .creator(instance.getCreator())
                .createdTime(instance.getCreatedTime())
                .updatedTime(instance.getUpdatedTime())
                .build();
    }

    /**
     * 转换为节点VO
     */
    private InstanceNodeVO convertToNodeVO(StructureInstanceNodeEntity node) {
        return InstanceNodeVO.builder()
                .id(node.getId().getId())
                .instanceId(node.getInstanceId().getId())
                .parentId(node.getParentId() != null ? node.getParentId().getId() : null)
                .nodeCode(node.getNodeCode())
                .nodeName(node.getNodeName())
                .nodeNameEn(node.getNodeNameEn())
                .nodeType(node.getNodeType().name())
                .categoryId(node.getCategoryId())
                .groupId(node.getGroupId())
                .usageId(node.getUsageId())
                .sortOrder(node.getSortOrder())
                .nodePath(node.getNodePath())
                .nodeLevel(node.getNodeLevel())
                .status(node.getStatus().name())
                .creator(node.getCreator())
                .createdTime(node.getCreatedTime())
                .updatedTime(node.getUpdatedTime())
                .build();
    }

    /**
     * 转换为节点树VO
     */
    private InstanceNodeTreeVO convertToNodeTreeVO(StructureInstanceNodeEntity node) {
        return InstanceNodeTreeVO.builder()
                .id(node.getId().getId())
                .nodeCode(node.getNodeCode())
                .nodeName(node.getNodeName())
                .nodeNameEn(node.getNodeNameEn())
                .nodeType(node.getNodeType().name())
                .categoryId(node.getCategoryId())
                .groupId(node.getGroupId())
                .usageId(node.getUsageId())
                .sortOrder(node.getSortOrder())
                .parentId(node.getParentId() != null ? node.getParentId().getId() : null)
                .nodePath(node.getNodePath())
                .nodeLevel(node.getNodeLevel())
                .children(new ArrayList<>())
                .build();
    }

    /**
     * 转换为差异VO列表
     */
    private List<InstanceNodeDiffVO> convertToDiffVOs(List<Map<String, Object>> diffItems, String diffType) {
        if (diffItems == null || diffItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<InstanceNodeDiffVO> result = new ArrayList<>();
        for (Map<String, Object> item : diffItems) {
            InstanceNodeDiffVO vo = new InstanceNodeDiffVO();
            vo.setNodeCode((String) item.get("nodeCode"));
            vo.setDiffType(diffType);
            if ("added".equals(diffType) || "removed".equals(diffType)) {
                StructureInstanceNodeEntity node = (StructureInstanceNodeEntity) item.get("node");
                vo.setNodeName(node.getNodeName());
                vo.setNodeType(node.getNodeType().getCode());
            } else if ("modified".equals(diffType)) {
                StructureInstanceNodeEntity oldNode = (StructureInstanceNodeEntity) item.get("oldNode");
                StructureInstanceNodeEntity newNode = (StructureInstanceNodeEntity) item.get("newNode");
                vo.setNodeName(newNode.getNodeName());
                vo.setNodeType(newNode.getNodeType().getCode());
                // 记录变更的字段
                List<String> changedFields = new ArrayList<>();
                if (!Objects.equals(oldNode.getNodeName(), newNode.getNodeName())) {
                    changedFields.add("nodeName: " + oldNode.getNodeName() + " -> " + newNode.getNodeName());
                }
                if (!Objects.equals(oldNode.getNodeNameEn(), newNode.getNodeNameEn())) {
                    changedFields.add("nodeNameEn: " + oldNode.getNodeNameEn() + " -> " + newNode.getNodeNameEn());
                }
                // 根据节点类型检查特定字段
                switch (oldNode.getNodeType()) {
                    case CATEGORY:
                        if (!Objects.equals(oldNode.getCategoryId(), newNode.getCategoryId())) {
                            changedFields.add("categoryId: " + oldNode.getCategoryId() + " -> " + newNode.getCategoryId());
                        }
                        break;
                    case GROUP:
                        if (!Objects.equals(oldNode.getGroupId(), newNode.getGroupId())) {
                            changedFields.add("groupId: " + oldNode.getGroupId() + " -> " + newNode.getGroupId());
                        }
                        break;
                    case USAGE:
                        if (!Objects.equals(oldNode.getUsageId(), newNode.getUsageId())) {
                            changedFields.add("usageId: " + oldNode.getUsageId() + " -> " + newNode.getUsageId());
                        }
                        break;
                }
                vo.setChangedFields(changedFields);
            }
            result.add(vo);
        }

        return result;
    }


}
