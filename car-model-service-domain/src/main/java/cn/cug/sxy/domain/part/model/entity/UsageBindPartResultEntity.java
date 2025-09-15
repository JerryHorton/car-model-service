package cn.cug.sxy.domain.part.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/5 10:30
 * @Description 用法备件批量上传结果实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageBindPartResultEntity {

    /**
     * 行号
     */
    private Integer rowNumber;
    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 备件ID（成功时返回）
     */
    private Long partId;
    /**
     * 用法ID（成功时返回）
     */
    private Long usageId;

}
