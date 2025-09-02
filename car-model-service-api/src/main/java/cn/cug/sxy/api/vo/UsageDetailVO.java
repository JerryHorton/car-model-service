package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/7 16:53
 * @Description 用法详情VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageDetailVO implements Serializable {

    /**
     * 配置组合列表
     */
    private List<CombinationDetail> combinations;

    /**
     * 配置组合详情
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CombinationDetail {

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
         * 配置项列表
         */
        private List<ConfigItemVO> configItems;

    }

}
