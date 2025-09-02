package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.WorkHourPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时数据访问层接口
 * @Author jerryhotton
 */

@Mapper
public interface IWorkHourDao {

    /**
     * 插入工时记录
     *
     * @param workHourPO 工时PO
     */
    void insert(WorkHourPO workHourPO);

    /**
     * 更新工时记录
     *
     * @param workHourPO 工时PO
     * @return 影响的行数
     */
    int update(WorkHourPO workHourPO);

    /**
     * 根据工时ID删除工时记录
     *
     * @param id 工时ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据工时ID查询工时记录
     *
     * @param id 工时ID
     * @return 工时PO
     */
    WorkHourPO selectById(Long id);

    /**
     * 根据工时代码查询工时记录
     *
     * @param code 工时代码
     * @return 工时PO
     */
    WorkHourPO selectByCode(String code);

    /**
     * 根据父ID查询子工时记录列表
     *
     * @param parentId 父工时ID
     * @return 工时PO列表
     */
    List<WorkHourPO> selectByParentId(Long parentId);

    /**
     * 查询所有主工时记录
     *
     * @return 工时PO列表
     */
    List<WorkHourPO> selectAllMainWorkHours();

    /**
     * 根据状态查询工时记录列表
     *
     * @param status 状态
     * @return 工时PO列表
     */
    List<WorkHourPO> selectByStatus(String status);

    /**
     * 根据类型查询工时记录列表
     *
     * @param type 类型
     * @return 工时PO列表
     */
    List<WorkHourPO> selectByType(String type);

    /**
     * 检查工时代码是否存在
     *
     * @param code 工时代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
