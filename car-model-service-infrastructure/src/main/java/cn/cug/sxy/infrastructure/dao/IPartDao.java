package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.PartPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/27 14:06
 * @Description 备件数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IPartDao {

    /**
     * 新增备件
     *
     * @param partPO 备件数据对象
     */
    void insert(PartPO partPO);

    /**
     * 更新备件
     *
     * @param partPO 备件数据对象
     * @return 更新影响的行数
     */
    int update(PartPO partPO);

    /**
     * 根据ID查询备件
     *
     * @param id 备件ID
     * @return 备件数据对象
     */
    PartPO selectById(Long id);

    /**
     * 根据编码查询备件
     *
     * @param partCode 备件编码
     * @return 备件数据对象
     */
    PartPO selectByCode(String partCode);

    /**
     * 检查备件编码是否存在
     *
     * @param partCode 备件编码
     * @return true 存在 false 不存在
     */
    boolean existsByCode(String partCode);

    /**
     * 查询所有备件
     *
     * @return 备件数据对象列表
     */
    List<PartPO> selectAll();

    /**
     * 根据状态查询备件
     *
     * @param status 状态码
     * @return 备件数据对象列表
     */
    List<PartPO> selectByStatus(String status);

    /**
     * 删除备件
     *
     * @param partId 备件ID
     * @return 删除影响的行数
     */
    int delete(Long partId);

}
