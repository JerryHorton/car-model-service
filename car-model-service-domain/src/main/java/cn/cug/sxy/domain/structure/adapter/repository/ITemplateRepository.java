package cn.cug.sxy.domain.structure.adapter.repository;

import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.domain.structure.model.valobj.StructureTemplatePageVO;
import cn.cug.sxy.domain.structure.model.valobj.TemplateCode;
import cn.cug.sxy.domain.structure.model.valobj.TemplateId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 18:35
 * @Description 车型结构树模板仓储接口
 * @Author jerryhotton
 */

public interface ITemplateRepository {

    /**
     * 保存模板
     *
     * @param template 模板实体
     */
    void save(StructureTemplateEntity template);

    /**
     * 更新模板
     *
     * @param template 模板实体
     * @return 更新的记录数
     */
    int update(StructureTemplateEntity template);

    /**
     * 更新模板状态
     *
     * @param templateId 模板ID
     * @param status     状态
     * @return 更新的记录数
     */
    int updateStatus(TemplateId templateId, Status status);

    /**
     * 根据ID查询模板
     *
     * @param templateId 模板ID
     * @return 模板实体
     */
    Optional<StructureTemplateEntity> findById(TemplateId templateId);

    /**
     * 根据模板编码查询所有版本
     *
     * @param templateCode 模板编码
     * @return 模板实体列表
     */
    List<StructureTemplateEntity> findByTemplateCode(TemplateCode templateCode);

    /**
     * 根据模板编码和版本查询
     *
     * @param templateCode 模板编码
     * @param version      版本
     * @return 模板实体
     */
    Optional<StructureTemplateEntity> findByTemplateCodeAndVersion(TemplateCode templateCode, String version);

    /**
     * 根据模板编码查询最新版本
     *
     * @param templateCode 模板编码
     * @return 模板实体
     */
    Optional<StructureTemplateEntity> findLatestVersionByCode(TemplateCode templateCode);

    /**
     * 查询所有模板
     *
     * @return 模板实体列表
     */
    List<StructureTemplateEntity> findAll();

    /**
     * 根据状态查询模板
     *
     * @param status 状态
     * @return 模板实体列表
     */
    List<StructureTemplateEntity> findByStatus(Status status);

    /**
     * 根据名称模糊查询模板
     *
     * @param nameKeyword 名称关键字
     * @return 模板实体列表
     */
    List<StructureTemplateEntity> findByNameLike(String nameKeyword);

    /**
     * 根据模板编码、状态和名称关键字查询模板
     *
     * @param templateCode 模板编码
     * @param status       状态
     * @param nameKeyword  名称关键字
     * @return 模板实体列表
     */
    List<StructureTemplateEntity> findTemplates(TemplateCode templateCode, Status status, String nameKeyword);

    /**
     * 分页查询模板列表
     *
     * @param templateCode 模板编码
     * @param status       状态
     * @param nameKeyword  名称关键字
     * @param pageNo       页码
     * @param pageSize     每页大小
     * @return 模板实体列表
     */
    StructureTemplatePageVO findTemplates(TemplateCode templateCode, Status status, String nameKeyword, int pageNo, int pageSize);

    /**
     * 检查模板编码是否存在
     *
     * @param templateCode 模板编码
     * @return 是否存在
     */
    boolean existsByCode(TemplateCode templateCode);

    /**
     * 检查模板编码和版本组合是否存在
     *
     * @param templateCode 模板编码
     * @param version      版本
     * @return 是否存在
     */
    boolean existsByCodeAndVersion(TemplateCode templateCode, String version);

    /**
     * 删除模板（逻辑删除，将状态更新为删除）
     *
     * @param templateId 模板ID
     * @return 更新的记录数
     */
    int deleteById(TemplateId templateId);

}
