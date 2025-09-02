package cn.cug.sxy.domain.usage.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/8/27 10:54
 * @Description 备件实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartEntity {

    /**
     * 主键ID
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
     * 状态（默认ENABLED）
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
