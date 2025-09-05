package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.PartHourPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/27 14:05
 * @Description 备件工时数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IPartHourDao {

    /**
     * 插入备件工时关联信息
     *
     * @param partHourPO 备件工时数据对象
     */
    void insert(PartHourPO partHourPO);

    /**
     * 根据备件ID删除备件工时关联信息
     *
     * @param partId 备件ID
     */
    int deleteByPartId(Long partId);

    /**
     * 根据备件ID和工时ID删除备件工时关联信息
     *
     * @param partId 备件ID
     * @param hourId 工时ID
     */
    int deleteByPartIdAndHourId(Long partId, Long hourId);

    /**
     * 检查备件工时关联信息是否存在
     *
     * @param partId 备件ID
     * @param hourId 工时ID
     * @return true 存在 false 不存在
     */
    boolean exists(@Param("partId") Long partId, @Param("hourId") Long hourId);

    /**
     * 根据备件ID查询备件工时关联信息
     *
     * @param partId 备件ID
     * @return 备件工时数据对象列表
     */
    List<PartHourPO> selectByPartId(Long partId);

}
