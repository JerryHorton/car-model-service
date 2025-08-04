package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/30 17:36
 * @Description 车型结构树实例持久化对象
 * @Author jerryhotton
 */

@Data
public class InstanceStructurePO {

    /**
     * 主键ID
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
     * 结构实例版本号
     */
    private String version;
    /**
     * 状态
     */
    private String status;
    /**
     * 是否已发布：0-未发布，1-已发布
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
