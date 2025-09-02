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
 * @Date 2025/8/6 18:48
 * @Description 用法创建结果VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageCreationVO implements Serializable {

    /**
     * 用法信息
     */
    private UsageInfo usage;
    /**
     * 配置组合列表
     */
    private List<CombinationInfo> combinations;
    /**
     * 实例节点信息
     */
    private InstanceNodeVO instanceNode;

    /**
     * 用法信息
     */
    @Data
    public static class UsageInfo {
        /**
         * 用法ID
         */
        private Long id;
        /**
         * 用法名称
         */
        private String usageName;
        /**
         * 爆炸图URL
         */
        private String explodedViewImg;
        /**
         * 下载URL
         */
        private String downloadUrl;
        /**
         * 状态
         */
        private String status;
        /**
         * 状态描述
         */
        private String statusDesc;
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

    /**
     * 配置组合信息
     */
    @Data
    public static class CombinationInfo {
        /**
         * 组合ID
         */
        private Long id;
        /**
         * 组合名称
         */
        private String combinationName;
        /**
         * 排序序号
         */
        private Integer sortOrder;
        /**
         * 配置项数量
         */
        private Integer configItemCount;
        /**
         * 创建时间
         */
        private LocalDateTime createdTime;
    }

}
