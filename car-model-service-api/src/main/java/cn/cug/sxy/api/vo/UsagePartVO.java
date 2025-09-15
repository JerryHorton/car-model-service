package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/5 10:51
 * @Description 用法备件关联VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsagePartVO {

    /**
     * 用法ID
     */
    private Long usageId;
    /**
     * 备件ID
     */
    private Long partId;
    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 备件名称
     */
    private String partName;
    /**
     * 数量
     */
    private Integer count;

}

