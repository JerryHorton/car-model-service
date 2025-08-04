package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/8/4 16:17
 * @Description 用法与配置多对多关系持久化对象
 * @Author jerryhotton
 */

@Data
public class GroupUsageConfigurationPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 分组用法ID
     */
    private Long usageId;
    /***
     * 配置ID
     */
    private Long configId;

}
