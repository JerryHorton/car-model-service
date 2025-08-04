package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 19:32
 * @Description 校验结果VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateValidateResultVO {

    /**
     * 是否有效
     */
    private boolean valid;
    /**
     * 问题列表
     */
    private List<String> issues;

}
