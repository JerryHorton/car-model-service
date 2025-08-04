package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.ConfigurationPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/4 16:18
 * @Description 车型配置数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IConfigurationDao {

    /**
     * 插入配置
     *
     * @param configuration 配置PO
     * @return 影响行数
     */
    int insert(ConfigurationPO configuration);

    /**
     * 批量插入配置
     *
     * @param configurations 配置PO列表
     * @return 影响行数
     */
    int insertBatch(List<ConfigurationPO> configurations);

    /**
     * 更新配置
     *
     * @param configuration 配置PO
     * @return 影响行数
     */
    int update(ConfigurationPO configuration);

    /**
     * 更新配置状态
     *
     * @param configuration 配置PO
     * @return 影响行数
     */
    int updateStatus(ConfigurationPO configuration);

    /**
     * 根据ID查询配置
     *
     * @param id 配置ID
     * @return 配置PO
     */
    ConfigurationPO selectById(Long id);

    /**
     * 根据配置编码查询配置
     *
     * @param configCode 配置编码
     * @return 配置PO
     */
    ConfigurationPO selectByCode(String configCode);

    /**
     * 根据配置名称模糊查询
     *
     * @param nameKeyword 名称关键字
     * @return 配置PO列表
     */
    List<ConfigurationPO> selectByNameLike(String nameKeyword);

    /**
     * 根据状态查询配置
     *
     * @param status 状态
     * @return 配置PO列表
     */
    List<ConfigurationPO> selectByStatus(String status);

    /**
     * 查询所有可用配置
     *
     * @return 配置PO列表
     */
    List<ConfigurationPO> selectAllEnabled();

    /**
     * 检查配置编码是否存在
     *
     * @param configCode 配置编码
     * @return 存在的记录数
     */
    int countByCode(String configCode);

    /**
     * 检查配置名称是否存在
     *
     * @param configName 配置名称
     * @return 存在的记录数
     */
    int countByName(String configName);

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 统计配置总数
     *
     * @return 配置总数
     */
    int count();

    /**
     * 统计指定状态的配置数量
     *
     * @param status 状态
     * @return 配置数量
     */
    int countByStatus(String status);

    /**
     * 根据条件查询配置
     *
     * @param configuration 配置PO
     * @return 配置PO列表
     */
    List<ConfigurationPO> selectByCondition(ConfigurationPO configuration);

}
