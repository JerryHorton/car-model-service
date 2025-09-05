package cn.cug.sxy.api.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件VO
 * @Author jerryhotton
 */
@Data
@Builder
public class PartVO {

    /**
     * 备件ID
     */
    private Long id;
    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 备件名称
     */
    private String partName;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 备注
     */
    private String remark;

} 