package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 19:17
 * @Description 车型结构树模板持久化对象
 * @Author jerryhotton
 */

@Data
public class TemplateStructurePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 模板编码
     */
    private String templateCode;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板描述
     */
    private String templateDesc;
    /**
     * 版本号
     */
    private String version;
    /**
     * 状态：禁用、启用、删除
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
