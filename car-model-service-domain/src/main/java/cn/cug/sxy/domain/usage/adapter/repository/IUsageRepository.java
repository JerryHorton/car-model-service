package cn.cug.sxy.domain.usage.adapter.repository;

import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/6 10:52
 * @Description 用法仓储接口
 * @Author jerryhotton
 */

public interface IUsageRepository {

    /**
     * 保存用法
     * <p>
     * 如果实体没有ID，则执行插入操作
     * 如果实体有ID，则执行更新操作
     *
     * @param usage 用法实体
     * @return 保存后的用法实体（包含生成的ID）
     */
    UsageEntity save(UsageEntity usage);

    /**
     * 保存用法实体（包含文件处理）
     * <p>
     * 在基础设施层处理文件上传，然后保存用法实体
     * 这样领域层不需要关心文件存储的具体实现
     *
     * @param usage            用法实体
     * @param explodedViewFile 爆炸图文件（可选）
     * @param groupId          系统分组ID（用于生成文件路径）
     * @return 保存后的用法实体（包含生成的ID和文件URL）
     */
    UsageEntity save(UsageEntity usage, MultipartFile explodedViewFile, Long groupId);

    /**
     * 根据ID查找用法
     *
     * @param usageId 用法ID
     * @return 用法实体（可能为空）
     */
    Optional<UsageEntity> findById(UsageId usageId);

    /**
     * 根据用法ID列表批量查询用法
     *
     * @param usageIds 用法ID列表
     * @return 用法实体列表
     */
    List<UsageEntity> findByIds(List<UsageId> usageIds);

    /**
     * 根据名称模糊查找用法
     * <p>
     * 支持用户通过用法名称搜索
     *
     * @param nameKeyword 名称关键字（支持模糊匹配）
     * @return 匹配的用法列表
     */
    List<UsageEntity> findByNameLike(String nameKeyword);

    /**
     * 查找所有可用的用法
     * <p>
     * 返回状态为ENABLED的用法，按ID排序
     *
     * @return 可用的用法列表
     */
    List<UsageEntity> findAllEnabled();

    /**
     * 删除用法
     * <p>
     * 注意：删除用法前应确保删除相关的配置组合和实例节点
     *
     * @param usageId 用法ID
     * @return 是否删除成功
     */
    boolean deleteById(UsageId usageId);

}
