package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/8/7 16:33
 * @Description 用法基础信息VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageBaseVO implements Serializable {

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

}
