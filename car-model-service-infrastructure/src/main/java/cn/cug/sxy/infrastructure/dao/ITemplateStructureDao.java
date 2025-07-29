package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.TemplateStructurePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 08:56
 * @Description 车型结构树模板数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface ITemplateStructureDao {

    /**
     * 插入模板
     *
     * @param templatePO 模板PO
     * @return 影响的行数
     */
    int insert(TemplateStructurePO templatePO);

    /**
     * 更新模板
     *
     * @param templatePO 模板PO
     * @return 影响的行数
     */
    int update(TemplateStructurePO templatePO);

    /**
     * 更新模板状态
     *
     * @param templateStructurePO 模板PO
     * @return 影响的行数
     */
    int updateStatus(TemplateStructurePO templateStructurePO);

    /**
     * 根据ID查询模板
     *
     * @param id 模板ID
     * @return 模板PO
     */
    TemplateStructurePO selectById(Long id);

    /**
     * 根据模板编码查询模板列表
     *
     * @param templateCode 模板编码
     * @return 模板PO列表
     */
    List<TemplateStructurePO> selectByTemplateCode(String templateCode);

    /**
     * 根据模板编码和版本查询模板
     *
     * @param templateStructurePO 查询条件
     * @return 模板PO
     */
    TemplateStructurePO selectByTemplateCodeAndVersion(TemplateStructurePO templateStructurePO);

    /**
     * 根据模板编码查询最新版本的模板
     *
     * @param templateCode 模板编码
     * @return 模板PO
     */
    TemplateStructurePO selectLatestVersionByCode(String templateCode);

    /**
     * 查询所有模板
     *
     * @return 模板PO列表
     */
    List<TemplateStructurePO> selectAll();

    /**
     * 根据状态查询模板
     *
     * @param status 状态
     * @return 模板PO列表
     */
    List<TemplateStructurePO> selectByStatus(String status);

    /**
     * 根据名称模糊查询模板
     *
     * @param nameKeyword 名称关键字
     * @return 模板PO列表
     */
    List<TemplateStructurePO> selectByNameLike(String nameKeyword);

    /**
     * 条件查询模板
     *
     * @param templateStructurePOReq 查询条件
     * @return 查询结果
     */
    List<TemplateStructurePO> selectByCondition(TemplateStructurePO templateStructurePOReq);

    /**
     * 检查模板编码是否存在
     *
     * @param templateCode 模板编码
     * @return 存在的记录数
     */
    int countByTemplateCode(String templateCode);

    /**
     * 检查模板编码和版本组合是否存在
     *
     * @param templateStructurePO 模板PO
     * @return 存在的记录数
     */
    int countByTemplateCodeAndVersion(TemplateStructurePO templateStructurePO);

}
