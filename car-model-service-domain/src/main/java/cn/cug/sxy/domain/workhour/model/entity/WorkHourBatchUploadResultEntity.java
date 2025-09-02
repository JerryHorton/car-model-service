package cn.cug.sxy.domain.workhour.model.entity;

import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/1 16:08
 * @Description 工时批量上传结果实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourBatchUploadResultEntity {

    /**
     * 行号（从1开始）
     */
    private Integer rowNumber;
    /**
     * 工时代码
     */
    private String code;
    /**
     * 工时描述
     */
    private String description;
    /**
     * 标准工时
     */
    private String standardHours;
    /**
     * 步骤顺序
     */
    private String stepOrder;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误信息（失败时）
     */
    private String errorMessage;
    /**
     * 创建的工时ID（成功时）
     */
    private Long workHourId;

}
