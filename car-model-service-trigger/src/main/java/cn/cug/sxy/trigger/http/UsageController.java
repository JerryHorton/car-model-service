package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IUsageService;
import cn.cug.sxy.api.dto.UsageCreateRequestDTO;
import cn.cug.sxy.api.dto.UsageUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.*;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.usage.model.aggregate.UsageAggregate;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationSpec;
import cn.cug.sxy.domain.usage.model.valobj.UsageCreationAggregate;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.domain.usage.service.IUsageManagementService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 18:51
 * @Description 用法服务
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/ap1/v1/usage/")
@DubboService(version = "1.0")
public class UsageController implements IUsageService {

    private final IUsageManagementService usageManagementService;

    public UsageController(IUsageManagementService usageManagementService) {
        this.usageManagementService = usageManagementService;
    }

    @RequestMapping(value = "create_usage", method = RequestMethod.POST)
    @Override
    public Response<UsageCreationVO> createUsage(@ModelAttribute @Valid UsageCreateRequestDTO requestDTO) {
        Long instanceId = requestDTO.getInstanceId();
        String usageName = requestDTO.getUsageName();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建用法 instanceId={}, usageName={}, creator={}", instanceId, usageName, creator);
            // 转换配置组合规格说明
            List<UsageConfigCombinationSpec> combinationSpecs = null;
            if (requestDTO.getCombinations() != null && !requestDTO.getCombinations().isEmpty()) {
                combinationSpecs = requestDTO.getCombinations().stream()
                        .map(this::convertToCombinationSpec)
                        .collect(Collectors.toList());
            }
            UsageCreationAggregate usageCreationAggregate = usageManagementService.createUsage(
                    instanceId,
                    requestDTO.getParentGroupNodeId(),
                    requestDTO.getGroupId(),
                    usageName,
                    requestDTO.getExplodedViewFile(),
                    requestDTO.getSortOrder(),
                    creator,
                    combinationSpecs
            );
            log.info("创建用法成功 instanceId={}, usageId={} usageName={}, creator={}",
                    instanceId, usageCreationAggregate.getUsage().getId().getId(), usageName, creator);
            // 转换VO
            UsageCreationVO vo = convertToCreationVO(usageCreationAggregate);

            return Response.<UsageCreationVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vo)
                    .build();
        } catch (AppException e) {
            log.error("创建用法失败 instanceId={}, usageName={}, creator={}", instanceId, usageName, creator, e);

            return Response.<UsageCreationVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建用法异常 instanceId={}, usageName={}, creator={}", instanceId, usageName, creator, e);

            return Response.<UsageCreationVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_usages", method = RequestMethod.GET)
    @Override
    public Response<List<UsageBaseVO>> queryUsages(@RequestParam Long groupNodeId) {
        try {
            if (groupNodeId == null) {
                throw new AppException("分组节点ID不能为空");
            }
            log.info("查询可用用法列表 groupNodeId={}", groupNodeId);
            List<UsageEntity> usageEntities = usageManagementService.findUsagesByGroup(groupNodeId);
            List<UsageBaseVO> usages = usageEntities.stream()
                    .map(this::convertToUsageBaseVO)
                    .collect(Collectors.toList());
            log.info("查询可用用法列表成功 groupNodeId={}, resultSize={}", groupNodeId, usages.size());

            return Response.<List<UsageBaseVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(usages)
                    .build();
        } catch (AppException e) {
            log.error("查询可用用法列表失败 groupNodeId={}", groupNodeId, e);

            return Response.<List<UsageBaseVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询可用用法列表异常 groupNodeId={}", groupNodeId, e);

            return Response.<List<UsageBaseVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_all_usages", method = RequestMethod.GET)
    @Override
    public Response<List<UsageBaseVO>> queryAllUsages(Long groupNodeId) {
        try {
            if (groupNodeId == null) {
                throw new AppException("分组节点ID不能为空");
            }
            log.info("查询用法列表（所有状态） groupNodeId={}", groupNodeId);
            List<UsageEntity> usageEntities = usageManagementService.findAllUsagesByGroup(groupNodeId);
            List<UsageBaseVO> usages = usageEntities.stream()
                    .map(this::convertToUsageBaseVO)
                    .collect(Collectors.toList());
            log.info("查询用法列表（所有状态）成功 groupNodeId={}, resultSize={}", groupNodeId, usages.size());

            return Response.<List<UsageBaseVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(usages)
                    .build();
        } catch (AppException e) {
            log.error("查询用法列表（所有状态）失败 groupNodeId={}", groupNodeId, e);

            return Response.<List<UsageBaseVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询用法列表（所有状态）异常 groupNodeId={}", groupNodeId, e);

            return Response.<List<UsageBaseVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_usage_detail", method = RequestMethod.GET)
    @Override
    public Response<UsageDetailVO> getUsageDetail(@RequestParam Long usageId) {
        try {
            if (usageId == null) {
                throw new AppException("用法ID不能为空");
            }
            log.info("查询用法详情 usageId={}", usageId);
            List<UsageConfigCombinationEntity> configCombinations = usageManagementService.findUsageDetail(new UsageId(usageId));
            UsageDetailVO usageDetailVO = convertToUsageDetailVO(configCombinations);
            log.info("查询用法详情成功 usageId={}", usageId);

            return Response.<UsageDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(usageDetailVO)
                    .build();
        } catch (AppException e) {
            log.error("查询用法详情失败 usageId={}", usageId, e);

            return Response.<UsageDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询用法详情异常 usageId={}", usageId, e);

            return Response.<UsageDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update_usage", method = RequestMethod.POST)
    @Override
    public Response<UsageBaseVO> updateUsage(UsageUpdateRequestDTO requestDTO) {
        Long usageId = requestDTO.getUsageId();
        try {
            if (usageId == null) {
                throw new AppException("用法ID不能为空");
            }
            log.info("更新用法 usageId={}", usageId);
            UsageAggregate usageAggregate = usageManagementService.updateUsage(
                    new UsageId(usageId),
                    requestDTO.getUsageName(),
                    requestDTO.getExplodedViewFile(),
                    requestDTO.getGroupId(),
                    requestDTO.getCombinations().stream()
                            .map(this::convertToCombinationSpec)
                            .collect(Collectors.toList())
            );
            UsageBaseVO vo = convertToUsageBaseVO(usageAggregate.getUsage());
            log.info("更新用法成功 usageId={}", usageId);

            return Response.<UsageBaseVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vo)
                    .build();
        } catch (AppException e) {
            log.error("更新用法失败 usageId={}", usageId, e);

            return Response.<UsageBaseVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("更新用法异常 usageId={}", usageId, e);

            return Response.<UsageBaseVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_usage", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deleteUsage(Long usageId) {
        try {
            if (usageId == null) {
                throw new AppException("用法ID不能为空");
            }
            log.info("删除用法 usageId={}", usageId);
            boolean deleted = usageManagementService.deleteUsage(new UsageId(usageId));
            log.info("删除用法成功 usageId={}, deleted={}", usageId, deleted);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(deleted)
                    .build();
        } catch (AppException e) {
            log.error("删除用法失败 usageId={}", usageId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除用法异常 usageId={}", usageId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "restore_usage", method = RequestMethod.POST)
    @Override
    public Response<Boolean> restoreUsage(Long usageId) {
        try {
            if (usageId == null) {
                throw new AppException("用法ID不能为空");
            }
            log.info("恢复用法 usageId={}", usageId);
            boolean restored = usageManagementService.restoreUsage(new UsageId(usageId));
            log.info("恢复用法成功 usageId={} restored={}", usageId, restored);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(restored)
                    .build();
        } catch (AppException e) {
            log.error("恢复用法失败 usageId={}", usageId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("恢复用法异常 usageId={}", usageId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 转换用法实体为基础VO
     */
    private UsageBaseVO convertToUsageBaseVO(UsageEntity usage) {
        UsageBaseVO vo = new UsageBaseVO();
        vo.setId(usage.getId().getId());
        vo.setUsageName(usage.getUsageName());
        vo.setExplodedViewImg(usage.getExplodedViewImg());
        vo.setDownloadUrl(usage.getDownloadUrl());
        vo.setStatus(usage.getStatus().name());

        return vo;
    }

    /**
     * 转换DTO为配置组合规格说明
     */
    private UsageConfigCombinationSpec convertToCombinationSpec(UsageCreateRequestDTO.CombinationCreateDTO dto) {
        List<ConfigItemId> configItemIds = dto.getConfigItemIds().stream()
                .map(ConfigItemId::new)
                .collect(Collectors.toList());

        return new UsageConfigCombinationSpec(dto.getCombinationName(), dto.getSortOrder(), configItemIds);
    }

    /**
     * 转换DTO为配置组合规格说明
     */
    private UsageConfigCombinationSpec convertToCombinationSpec(UsageUpdateRequestDTO.CombinationUpdateDTO dto) {
        List<ConfigItemId> configItemIds = dto.getConfigItemIds().stream()
                .map(ConfigItemId::new)
                .collect(Collectors.toList());

        return new UsageConfigCombinationSpec(dto.getCombinationName(), dto.getSortOrder(), configItemIds);
    }

    /**
     * 转换配置组合实体为组合信息VO
     */
    private UsageCreationVO.CombinationInfo convertToCombinationInfo(UsageConfigCombinationEntity combination) {
        UsageCreationVO.CombinationInfo info = new UsageCreationVO.CombinationInfo();
        info.setId(combination.getId().getId());
        info.setCombinationName(combination.getCombinationName());
        info.setSortOrder(combination.getSortOrder());
        info.setConfigItemCount(combination.getConfigItemIds() != null ? combination.getConfigItemIds().size() : 0);
        info.setCreatedTime(combination.getCreatedTime());
        return info;
    }

    /**
     * 转换用法创建结果为VO
     */
    private UsageCreationVO convertToCreationVO(UsageCreationAggregate usageCreationAggregate) {
        UsageCreationVO vo = new UsageCreationVO();
        // 转换用法信息
        UsageCreationVO.UsageInfo usageInfo = new UsageCreationVO.UsageInfo();
        UsageEntity usage = usageCreationAggregate.getUsage();
        usageInfo.setId(usage.getId().getId());
        usageInfo.setUsageName(usage.getUsageName());
        usageInfo.setExplodedViewImg(usage.getExplodedViewImg());
        usageInfo.setDownloadUrl(usage.getDownloadUrl());
        usageInfo.setStatus(usage.getStatus().name());
        usageInfo.setStatusDesc(usage.getStatus().name().equals("ENABLED") ? "启用" : "禁用");
        usageInfo.setCreator(usage.getCreator());
        usageInfo.setCreatedTime(usage.getCreatedTime());
        usageInfo.setUpdatedTime(usage.getUpdatedTime());
        vo.setUsage(usageInfo);
        // 转换配置组合信息
        if (usageCreationAggregate.getCombinations() != null && !usageCreationAggregate.getCombinations().isEmpty()) {
            List<UsageCreationVO.CombinationInfo> combinationInfos = usageCreationAggregate.getCombinations().stream()
                    .map(this::convertToCombinationInfo)
                    .collect(Collectors.toList());
            vo.setCombinations(combinationInfos);
        }
        // 转换实例节点信息
        StructureInstanceNodeEntity instanceNode = usageCreationAggregate.getInstanceNode();
        InstanceNodeVO nodeVO = new InstanceNodeVO();
        nodeVO.setId(instanceNode.getId().getId());
        nodeVO.setInstanceId(instanceNode.getInstanceId().getId());
        nodeVO.setParentId(instanceNode.getParentId() != null ? instanceNode.getParentId().getId() : null);
        nodeVO.setNodeCode(instanceNode.getNodeCode());
        nodeVO.setNodeName(instanceNode.getNodeName());
        nodeVO.setNodeNameEn(instanceNode.getNodeNameEn());
        nodeVO.setNodeType(instanceNode.getNodeType().name());
        nodeVO.setUsageId(instanceNode.getUsageId());
        nodeVO.setSortOrder(instanceNode.getSortOrder());
        nodeVO.setNodePath(instanceNode.getNodePath());
        nodeVO.setNodeLevel(instanceNode.getNodeLevel());
        nodeVO.setStatus(instanceNode.getStatus().name());
        nodeVO.setCreator(instanceNode.getCreator());
        nodeVO.setCreatedTime(instanceNode.getCreatedTime());
        nodeVO.setUpdatedTime(instanceNode.getUpdatedTime());
        vo.setInstanceNode(nodeVO);

        return vo;
    }

    /**
     * 转换用法详情为VO
     */
    private UsageDetailVO convertToUsageDetailVO(List<UsageConfigCombinationEntity> combinations) {
        UsageDetailVO vo = new UsageDetailVO();
        List<UsageDetailVO.CombinationDetail> combinationDetails = combinations.stream()
                .map(combination -> {
                    UsageDetailVO.CombinationDetail detail = new UsageDetailVO.CombinationDetail();
                    detail.setId(combination.getId().getId());
                    detail.setCombinationName(combination.getCombinationName());
                    detail.setSortOrder(combination.getSortOrder());
                    detail.setConfigItems(combination.getConfigItemDetails().stream()
                            .map(this::convertToItemVO)
                            .collect(Collectors.toList()));
                    return detail;
                }).collect(Collectors.toList());
        vo.setCombinations(combinationDetails);

        return vo;
    }

    /**
     * 转换配置项实体为VO
     */
    private ConfigItemVO convertToItemVO(ConfigItemEntity item) {
        ConfigItemVO vo = new ConfigItemVO();
        vo.setId(item.getId().getId());
        vo.setCategoryId(item.getCategoryId().getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setItemValue(item.getItemValue());
        vo.setStatus(item.getStatus().name());
        vo.setCreator(item.getCreator());
        vo.setCreatedTime(item.getCreatedTime());
        vo.setUpdatedTime(item.getUpdatedTime());

        return vo;
    }

}
