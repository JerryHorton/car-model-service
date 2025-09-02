package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.ConfigItemPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/5 18:51
 * @Description 配置项数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IConfigItemDao {

    /**
     * 插入配置项
     *
     * @param itemPO 配置项PO
     * @return 影响的行数
     */
    int insert(ConfigItemPO itemPO);

    /**
     * 更新配置项
     *
     * @param itemPO 配置项PO
     * @return 影响的行数
     */
    int update(ConfigItemPO itemPO);

    /**
     * 根据ID查询配置项
     *
     * @param id 配置项ID
     * @return 配置项PO
     */
    ConfigItemPO selectById(Long id);

    /**
     * 根据类别ID查询可用的配置项
     *
     * @param categoryId 类别ID
     * @return 配置项列表
     */
    List<ConfigItemPO> selectEnabledByCategoryId(Long categoryId);

    /**
     * 根据配置项编码查询配置项
     *
     * @param itemCode 配置项编码
     * @return 配置项PO
     */
    ConfigItemPO selectByCode(String itemCode);

    /**
     * 根据配置值或名称模糊查询配置项
     *
     * @param keyword 配置值或名称关键字
     * @return 配置项列表
     */
    List<ConfigItemPO> selectByValueLikeOrNameLike(String keyword);

    /**
     * 根据多个ID批量查询配置项
     *
     * @param ids 配置项ID列表
     * @return 配置项列表
     */
    List<ConfigItemPO> selectByIds(List<Long> ids);

    /**
     * 检查配置项编码是否存在
     *
     * @param itemCode 配置项编码
     * @return 存在的记录数
     */
    int countByCode(String itemCode);

    /**
     * 删除配置项
     *
     * @param id 配置项ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据类别ID删除所有配置项
     *
     * @param categoryId 类别ID
     * @return 影响的行数
     */
    int deleteByCategoryId(Long categoryId);

}
