package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.dao.po.UsagePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/5 18:49
 * @Description 用法数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IUsageDao {

    /**
     * 插入用法
     *
     * @param usagePO 用法PO
     * @return 影响的行数
     */
    int insert(UsagePO usagePO);

    /**
     * 更新用法
     *
     * @param usagePO 用法PO
     * @return 影响的行数
     */
    int update(UsagePO usagePO);

    /**
     * 更新用法状态
     *
     * @param id     用法ID
     * @param status 状态
     * @return 影响的行数
     */
    int updateStatus(Long id, String status);

    /**
     * 根据ID查询用法
     *
     * @param id 用法ID
     * @return 用法PO
     */
    UsagePO selectById(Long id);

    /**
     * 根据名称模糊查询用法
     *
     * @param nameKeyword 名称关键字
     * @return 用法列表
     */
    List<UsagePO> selectByNameLike(String nameKeyword);

    /**
     * 查询所有可用的用法
     *
     * @return 用法列表
     */
    List<UsagePO> selectAllEnabled();

    /**
     * 删除用法
     *
     * @param id 用法ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据用法ID列表批量查询用法
     *
     * @param usageIds 用法ID列表
     * @return 用法列表
     */
    List<UsagePO> selectByIds(List<Long> usageIds);

}
