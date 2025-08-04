package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/31 16:26
 * @Description 校验结果VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceValidateResultVO {

    /**
     * 是否有效
     */
    private Boolean valid;
    /**
     * 问题列表
     */
    private List<String> issues;

}
