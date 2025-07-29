package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.SystemCategoryPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/28 08:55
 * @Description 系统大类DAO接口
 * @Author jerryhotton
 */

@Mapper
public interface ISystemCategoryDao {

    /**
     * 插入系统大类
     *
     * @param systemCategoryPO 系统大类PO
     * @return 影响行数
     */
    int insert(SystemCategoryPO systemCategoryPO);

    /**
     * 更新系统大类
     *
     * @param systemCategoryPO 系统大类PO
     * @return 影响行数
     */
    int update(SystemCategoryPO systemCategoryPO);

    /**
     * 根据ID查询系统大类
     *
     * @param id 大类ID
     * @return 系统大类PO
     */
    SystemCategoryPO selectById(Long id);

    /**
     * 根据大类编码查询系统大类
     *
     * @param categoryCode 大类编码
     * @return 系统大类PO
     */
    SystemCategoryPO selectByCategoryCode(String categoryCode);

    /**
     * 查询所有系统大类
     *
     * @return 系统大类PO列表
     */
    List<SystemCategoryPO> selectAll();

    /**
     * 根据状态查询系统大类
     *
     * @param status 状态编码
     * @return 系统大类PO列表
     */
    List<SystemCategoryPO> selectByStatus(String status);

    /**
     * 根据名称模糊查询系统大类
     *
     * @param categoryNameLike 大类名称（模糊匹配）
     * @return 系统大类PO列表
     */
    List<SystemCategoryPO> selectByCategoryNameLike(String categoryNameLike);

    /**
     * 统计指定大类编码的记录数
     *
     * @param categoryCode 大类编码
     * @return 记录数
     */
    int countByCategoryCode(String categoryCode);

}
