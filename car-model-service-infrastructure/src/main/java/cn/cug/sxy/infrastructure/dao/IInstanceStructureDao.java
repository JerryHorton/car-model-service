package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.InstanceStructurePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/30 17:34
 * @Description 车型结构树实例数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IInstanceStructureDao {

    /**
     * 插入实例
     *
     * @param instancePO 实例PO
     * @return 影响的行数
     */
    int insert(InstanceStructurePO instancePO);

    /**
     * 更新实例
     *
     * @param instancePO 实例PO
     * @return 影响的行数
     */
    int update(InstanceStructurePO instancePO);

    /**
     * 更新实例状态
     *
     * @param instancePO 实例PO
     * @return 影响的行数
     */
    int updateStatus(InstanceStructurePO instancePO);

    /**
     * 更新实例发布状态
     *
     * @param instancePO 实例PO
     * @return 影响的行数
     */
    int updatePublishStatus(InstanceStructurePO instancePO);

    /**
     * 根据ID查询实例
     *
     * @param id 实例ID
     * @return 实例PO
     */
    InstanceStructurePO selectById(Long id);

    /**
     * 根据实例编码查询实例列表
     *
     * @param instanceCode 实例编码
     * @return 实例PO列表
     */
    List<InstanceStructurePO> selectByInstanceCode(String instanceCode);

    /**
     * 根据实例编码和版本查询实例
     *
     * @param instancePO 实例PO
     * @return 实例PO
     */
    InstanceStructurePO selectByInstanceCodeAndVersion(InstanceStructurePO instancePO);

    /**
     * 根据车系ID查询实例列表
     *
     * @param seriesId 车系ID
     * @return 实例PO列表
     */
    List<InstanceStructurePO> selectBySeriesId(Long seriesId);

    /**
     * 根据车型ID查询实例列表
     *
     * @param modelId 车型ID
     * @return 实例PO列表
     */
    List<InstanceStructurePO> selectByModelId(Long modelId);

    /**
     * 根据状态查询实例列表
     *
     * @param status 状态
     * @return 实例PO列表
     */
    List<InstanceStructurePO> selectByStatus(String status);

    /**
     * 查询所有实例
     *
     * @return 实例PO列表
     */
    List<InstanceStructurePO> selectAll();

    /**
     * 根据条件查询实例列表
     *
     * @param instancePO 实例PO
     * @return 实例PO列表
     */
    List<InstanceStructurePO> selectByCondition(InstanceStructurePO instancePO);

    /**
     * 删除实例
     *
     * @param id 实例ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 检查实例编码是否存在
     *
     * @param instanceCode 实例编码
     * @return 存在的记录数
     */
    int countByInstanceCode(String instanceCode);

    /**
     * 检查实例编码和版本组合是否存在
     *
     * @param instancePO 实例PO
     * @return 存在的记录数
     */
    int countByInstanceCodeAndVersion(InstanceStructurePO instancePO);

}
