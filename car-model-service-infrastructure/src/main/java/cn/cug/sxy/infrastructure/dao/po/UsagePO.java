package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 18:48
 * @Description 用法PO
 * @Author jerryhotton
 */

@Data
public class UsagePO {

    /**
     * 主键ID（同时是业务ID）
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
