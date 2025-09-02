package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 19:26
 * @Description 配置类别VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigCategoryVO implements Serializable {

    /**
     * 配置类别ID
     */
    private Long id;
    /**
     * 类别编码
     */
    private String categoryCode;
    /**
     * 类别名称
     */
    private String categoryName;
    /**
     * 排序序号
     */
    private Integer sortOrder;
    /**
     * 状态
     */
    private String status;
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
    /**
     * 配置项列表（可选，用于详情页面）
     */
    private List<ConfigItemVO> items;

}
