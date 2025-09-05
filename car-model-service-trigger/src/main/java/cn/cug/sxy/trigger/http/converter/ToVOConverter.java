package cn.cug.sxy.trigger.http.converter;

import cn.cug.sxy.api.vo.*;
import cn.cug.sxy.domain.part.model.entity.PartBindHourResultEntity;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourBatchUploadResultEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/4 11:31
 * @Description VO转换器
 * @Author jerryhotton
 */

public class ToVOConverter {

    /**
     * 将批量绑定结果实体转换为VO
     */
    public static PartBindHourResultVO convertToPartBindHourResultVO(PartBindHourResultEntity entity) {
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
    public static PartVO convertToPartVO(PartEntity entity) {
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
    public static List<PartVO> convertToPartVOList(List<PartEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(ToVOConverter::convertToPartVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换工时实体为VO
     */
    public static WorkHourVO convertToWorkHourVO(WorkHourEntity entity) {
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
    public static WorkHourTreeVO convertToWorkHourTreeVO(WorkHourEntity entity) {
        if (entity == null) {
            return null;
        }
        WorkHourTreeVO workHourTreeVO = new WorkHourTreeVO();
        WorkHourVO workHourVO = convertToWorkHourVO(entity);
        workHourTreeVO.setWorkHourVO(workHourVO);
        // 递归转换子工时
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            List<WorkHourVO> childrenVOs = entity.getChildren().stream()
                    .map(ToVOConverter::convertToWorkHourVO)
                    .collect(Collectors.toList());
            workHourTreeVO.setChildren(childrenVOs);
        }

        return workHourTreeVO;
    }

    /**
     * 转换批量上传结果实体为VO
     */
    public static WorkHourBatchUploadResultVO convertToWorkHourBatchUploadResultVO(WorkHourBatchUploadResultEntity entity) {
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

}
