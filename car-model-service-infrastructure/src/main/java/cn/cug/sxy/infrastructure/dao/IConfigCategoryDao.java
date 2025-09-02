package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.ConfigCategoryPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/5 18:50
 * @Description 配置类别数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IConfigCategoryDao {

    /**
     * 插入配置类别
     *
     * @param categoryPO 配置类别PO
     * @return 影响的行数
     */
    int insert(ConfigCategoryPO categoryPO);

    /**
     * 更新配置类别
     *
     * @param categoryPO 配置类别PO
     * @return 影响的行数
     */
    int update(ConfigCategoryPO categoryPO);

    /**
     * 根据ID查询配置类别
     *
     * @param id 配置类别ID
     * @return 配置类别PO
     */
    ConfigCategoryPO selectById(Long id);

    /**
     * 根据类别编码查询配置类别
     *
     * @param categoryCode 类别编码
     * @return 配置类别PO
     */
    ConfigCategoryPO selectByCode(String categoryCode);

    /**
     * 查询所有可用的配置类别
     *
     * @return 配置类别列表
     */
    List<ConfigCategoryPO> selectAllEnabled();

    /**
     * 根据条件查询配置类别
     *
     * @param categoryPO 配置类别PO
     * @return 配置类别列表
     */
    List<ConfigCategoryPO> selectByCondition(ConfigCategoryPO categoryPO);

    /**
     * 检查类别编码是否存在
     *
     * @param categoryCode 类别编码
     * @return 存在的记录数
     */
    int countByCode(String categoryCode);

    /**
     * 删除配置类别
     *
     * @param id 配置类别ID
     * @return 影响的行数
     */
    int deleteById(Long id);

}
