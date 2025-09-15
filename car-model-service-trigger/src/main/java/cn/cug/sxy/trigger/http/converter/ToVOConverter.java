package cn.cug.sxy.trigger.http.converter;

import cn.cug.sxy.api.vo.*;
import cn.cug.sxy.domain.part.model.entity.PartBindHourResultEntity;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.entity.UsageBindPartResultEntity;
import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.service.ISystemCategoryService;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourBatchUploadResultEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/4 11:31
 * @Description VO转换器
 * @Author jerryhotton
 */

@Component
public class ToVOConverter {

    private final ISystemCategoryService systemCategoryService;

    public ToVOConverter(ISystemCategoryService systemCategoryService) {
        this.systemCategoryService = systemCategoryService;
    }

    /**
     * 将批量绑定结果实体转换为VO
     */
    public PartBindHourResultVO convertToPartBindHourResultVO(PartBindHourResultEntity entity) {
        if (entity == null) {
            return null;
        }

        return PartBindHourResultVO.builder()
                .rowNumber(entity.getRowNumber())
                .partCode(entity.getPartCode())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .workHourCode(entity.getWorkHourCode())
                .build();
    }

    /**
     * 将备件实体转换为VO
     */
    public PartVO convertToPartVO(PartEntity entity) {
        if (entity == null) {
            return null;
        }

        return PartVO.builder()
                .id(entity.getId() != null ? entity.getId().getId() : null)
                .partCode(entity.getCode() != null ? entity.getCode().getCode() : null)
                .partName(entity.getName())
                .status(entity.getStatus() != null ? entity.getStatus().getCode() : null)
                .creator(entity.getCreator())
                .remark(entity.getRemark())
                .build();
    }

    /**
     * 将备件实体列表转换为VO列表
     */
    public List<PartVO> convertToPartVOList(List<PartEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::convertToPartVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换工时实体为VO
     */
    public WorkHourVO convertToWorkHourVO(WorkHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkHourVO.builder()
                .id(entity.getId() != null ? entity.getId().getId() : null)
                .parentId(entity.getParentId() != null ? entity.getParentId().getId() : null)
                .code(entity.getCode() != null ? entity.getCode().getCode() : null)
                .description(entity.getDescription())
                .standardHours(entity.getStandardHours())
                .type(entity.getType() != null ? entity.getType().getCode() : null)
                .typeDescription(entity.getType() != null ? entity.getType().getDescription() : null)
                .stepOrder(entity.getStepOrder())
                .status(entity.getStatus() != null ? entity.getStatus().getCode() : null)
                .creator(entity.getCreator())
                .isMainWorkHour(entity.isMainWorkHour())
                .isSubWorkHour(entity.isSubWorkHour())
                .build();
    }

    /**
     * 转换工时实体为VO
     */
    public WorkHourTreeVO convertToWorkHourTreeVO(WorkHourEntity entity) {
        if (entity == null) {
            return null;
        }
        WorkHourTreeVO workHourTreeVO = new WorkHourTreeVO();
        WorkHourVO workHourVO = convertToWorkHourVO(entity);
        workHourTreeVO.setWorkHourVO(workHourVO);
        // 递归转换子工时
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            List<WorkHourVO> childrenVOs = entity.getChildren().stream()
                    .map(this::convertToWorkHourVO)
                    .collect(Collectors.toList());
            workHourTreeVO.setChildren(childrenVOs);
        }

        return workHourTreeVO;
    }

    /**
     * 转换批量上传结果实体为VO
     */
    public WorkHourBatchUploadResultVO convertToWorkHourBatchUploadResultVO(WorkHourBatchUploadResultEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkHourBatchUploadResultVO.builder()
                .rowNumber(entity.getRowNumber())
                .code(entity.getCode())
                .description(entity.getDescription())
                .standardHours(entity.getStandardHours())
                .stepOrder(entity.getStepOrder())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .workHourId(entity.getWorkHourId())
                .build();
    }

    /**
     * 转换用法备件关联实体为VO
     */
    public UsagePartVO convertToUsagePartVO(UsagePartEntity entity) {
        if (entity == null) {
            return null;
        }

        return UsagePartVO.builder()
                .usageId(entity.getUsageId() != null ? entity.getUsageId().getId() : null)
                .partId(entity.getPartId() != null ? entity.getPartId().getId() : null)
                .partCode(entity.getPartCode())
                .partName(entity.getPartName())
                .count(entity.getCount())
                .build();
    }

    /**
     * 转换用法备件关联实体列表为VO列表
     */
    public List<UsagePartVO> convertToUsagePartVOList(List<UsagePartEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::convertToUsagePartVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换用法备件批量上传结果实体为VO
     */
    public UsageBindPartResultVO convertToUsageBindPartResultVO(UsageBindPartResultEntity entity) {
        if (entity == null) {
            return null;
        }

        return UsageBindPartResultVO.builder()
                .rowNumber(entity.getRowNumber())
                .partCode(entity.getPartCode())
                .count(entity.getCount())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .partId(entity.getPartId())
                .usageId(entity.getUsageId())
                .build();
    }

    /**
     * 转换系统大类实体为VO
     */
    public SystemCategoryVO convertToCategoryVO(SystemCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        SystemCategoryVO vo = new SystemCategoryVO();
        vo.setId(entity.getCategoryId().getId());
        vo.setCategoryCode(entity.getCategoryCode().getCode());
        vo.setCategoryName(entity.getCategoryName());
        vo.setCategoryNameEn(entity.getCategoryNameEn());
        vo.setSortOrder(entity.getSortOrder());
        vo.setStatus(entity.getStatus().name());
        vo.setCreator(entity.getCreator());

        return vo;
    }

    /**
     * 转换系统分组实体为VO
     */
    public SystemGroupVO convertToGroupVO(SystemGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        // 查询所属大类信息
        SystemCategoryEntity categoryEntity = null;
        if (entity.getCategoryId() != null) {
            categoryEntity = systemCategoryService.findById(entity.getCategoryId());
        }
        SystemGroupVO vo = new SystemGroupVO();
        vo.setId(entity.getGroupId().getId());
        vo.setCategoryId(entity.getCategoryId().getId());
        vo.setGroupCode(entity.getGroupCode().getCode());
        vo.setGroupName(entity.getGroupName());
        vo.setGroupNameEn(entity.getGroupNameEn());
        vo.setSortOrder(entity.getSortOrder());
        vo.setStatus(entity.getStatus().name());
        vo.setCreator(entity.getCreator());
        // 设置大类相关信息
        if (categoryEntity != null) {
            vo.setCategoryCode(categoryEntity.getCategoryCode().getCode());
            vo.setCategoryName(categoryEntity.getCategoryName());
        }

        return vo;
    }

}
