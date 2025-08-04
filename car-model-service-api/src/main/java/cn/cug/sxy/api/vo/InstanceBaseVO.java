package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/31 16:25
 * @Description 实例基本VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceBaseVO {

    /**
     * 实例ID
     */
    private Long id;
    /**
     * 实例编码
     */
    private String instanceCode;
    /**
     * 实例名称
     */
    private String instanceName;
    /**
     * 实例描述
     */
    private String instanceDesc;
    /**
     * 车型系列ID
     */
    private Long seriesId;
    /**
     * 车型ID
     */
    private Long modelId;
    /**
     * 实例版本
     */
    private String instanceVersion;
    /**
     * 状态
     */
    private String status;
    /**
     * 是否已发布
     */
    private Boolean isPublished;
    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
