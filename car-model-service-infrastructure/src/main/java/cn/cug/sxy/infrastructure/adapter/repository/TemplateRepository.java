package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.structure.adapter.repository.ITemplateRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.domain.structure.model.valobj.StructureTemplatePageVO;
import cn.cug.sxy.domain.structure.model.valobj.TemplateCode;
import cn.cug.sxy.domain.structure.model.valobj.TemplateId;
import cn.cug.sxy.infrastructure.converter.TemplateStructureConverter;
import cn.cug.sxy.infrastructure.dao.ITemplateStructureDao;
import cn.cug.sxy.infrastructure.dao.po.TemplateStructurePO;
import cn.cug.sxy.infrastructure.redis.IRedisService;
import cn.cug.sxy.types.common.Constants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @version 1.0
 * @Date 2025/7/28 18:37
 * @Description 车型结构树模板仓储实现
 * @Author jerryhotton
 */

@Repository
public class TemplateRepository extends AbstractRepository implements ITemplateRepository {

    private final ITemplateStructureDao templateStructureDao;
    private final IRedisService redisService;

    public TemplateRepository(
            ITemplateStructureDao templateStructureDao,
            IRedisService redisService) {
        this.templateStructureDao = templateStructureDao;
        this.redisService = redisService;
    }

    @Override
    public void save(StructureTemplateEntity template) {
        if (template == null) {
            return;
        }
        TemplateStructurePO po = TemplateStructureConverter.toPO(template);
        templateStructureDao.insert(po);
        // 为插入后的实体设置ID
        if (po.getId() != null && template.getId() == null) {
            template.setId(new TemplateId(po.getId()));
        }
        // 清除相关缓存
        clearCacheOnSave(template);
    }

    @Override
    public int update(StructureTemplateEntity template) {
        if (template == null || template.getId() == null) {
            return 0;
        }
        // 清除相关缓存
        clearCacheOnUpdate(template);
        TemplateStructurePO po = TemplateStructureConverter.toPO(template);

        return templateStructureDao.update(po);
    }

    @Override
    public int updateStatus(TemplateId templateId, Status status) {
        if (templateId == null || status == null) {
            return 0;
        }
        // 构建查询条件
        TemplateStructurePO templateStructurePOReq = new TemplateStructurePO();
        templateStructurePOReq.setId(templateId.getId());
        templateStructurePOReq.setStatus(status.getCode());
        int result = templateStructureDao.updateStatus(templateStructurePOReq);
        // 清除相关缓存
        if (result > 0) {
            redisService.remove(getTemplateByIdCacheKey(templateId.getId()));
            clearRelationCache(templateId);
        }

        return result;
    }

    @Override
    public Optional<StructureTemplateEntity> findById(TemplateId templateId) {
        if (templateId == null) {
            return Optional.empty();
        }
        String cacheKey = getTemplateByIdCacheKey(templateId.getId());
        TemplateStructurePO templateStructurePO = getDataFromCacheOrDB(cacheKey, () -> templateStructureDao.selectById(templateId.getId()));
        if (templateStructurePO == null) {
            return Optional.empty();
        }
        StructureTemplateEntity entity = TemplateStructureConverter.toEntity(templateStructurePO);

        return Optional.of(entity);
    }

    @Override
    public List<StructureTemplateEntity> findByTemplateCode(TemplateCode templateCode) {
        if (templateCode == null) {
            return java.util.Collections.emptyList();
        }
        List<TemplateStructurePO> poList = templateStructureDao.selectByTemplateCode(templateCode.getCode());
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return TemplateStructureConverter.toEntityList(poList);
    }

    @Override
    public Optional<StructureTemplateEntity> findByTemplateCodeAndVersion(TemplateCode templateCode, String version) {
        if (templateCode == null || version == null) {
            return Optional.empty();
        }
        String cacheKey = getTemplateByCodeAndVersionCacheKey(templateCode.getCode(), version);
        TemplateStructurePO templateStructurePOReq = new TemplateStructurePO();
        templateStructurePOReq.setTemplateCode(templateCode.getCode());
        templateStructurePOReq.setVersion(version);
        TemplateStructurePO templateStructurePO = getDataFromCacheOrDB(cacheKey, () -> templateStructureDao.selectByTemplateCodeAndVersion(templateStructurePOReq));
        if (templateStructurePO == null) {
            return Optional.empty();
        }
        StructureTemplateEntity entity = TemplateStructureConverter.toEntity(templateStructurePO);

        return Optional.of(entity);
    }

    @Override
    public Optional<StructureTemplateEntity> findLatestVersionByCode(TemplateCode templateCode) {
        if (templateCode == null) {
            return Optional.empty();
        }
        String cacheKey = getTemplateByCodeCacheKey(templateCode.getCode());
        TemplateStructurePO templateStructurePO = getDataFromCacheOrDB(cacheKey, () -> templateStructureDao.selectLatestVersionByCode(templateCode.getCode()));
        if (templateStructurePO == null) {
            return Optional.empty();
        }
        StructureTemplateEntity entity = TemplateStructureConverter.toEntity(templateStructurePO);

        return Optional.of(entity);
    }

    @Override
    public List<StructureTemplateEntity> findAll() {
        String cacheKey = getTemplateAllCacheKey();
        List<TemplateStructurePO> templateStructurePOList = getDataFromCacheOrDB(cacheKey, templateStructureDao::selectAll);
        if (templateStructurePOList == null || templateStructurePOList.isEmpty()) {
            return Collections.emptyList();
        }

        return TemplateStructureConverter.toEntityList(templateStructurePOList);
    }

    @Override
    public List<StructureTemplateEntity> findTemplates(TemplateCode templateCode, Status status, String nameKeyword) {
        if (nameKeyword != null) {
            nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
            nameKeyword = "%" + nameKeyword + "%";
        }
        TemplateStructurePO templateStructurePOReq = new TemplateStructurePO();
        templateStructurePOReq.setTemplateCode(null == templateCode ? null : templateCode.getCode());
        templateStructurePOReq.setStatus(null == status ? null : status.getCode());
        templateStructurePOReq.setTemplateName(nameKeyword);
        List<TemplateStructurePO> poList = templateStructureDao.selectByCondition(templateStructurePOReq);

        return TemplateStructureConverter.toEntityList(poList);
    }

    @Override
    public StructureTemplatePageVO findTemplates(TemplateCode templateCode, Status status, String nameKeyword, int pageNo, int pageSize) {
        // 启用分页
        PageHelper.startPage(pageNo, pageSize);
        // 查询模板列表
        List<StructureTemplateEntity> result = findTemplates(templateCode, status, nameKeyword);
        // 构建分页信息
        PageInfo<StructureTemplateEntity> pageInfo = new PageInfo<>(result);

        return StructureTemplatePageVO.builder()
                .templates(result)
                .total(pageInfo.getTotal())
                .totalPages(pageInfo.getPages())
                .currentPage(pageInfo.getPageNum())
                .build();
    }

    @Override
    public List<StructureTemplateEntity> findByStatus(Status status) {
        if (status == null) {
            return java.util.Collections.emptyList();
        }

        List<TemplateStructurePO> poList = templateStructureDao.selectByStatus(status.getCode());
        return TemplateStructureConverter.toEntityList(poList);
    }

    @Override
    public List<StructureTemplateEntity> findByNameLike(String nameKeyword) {
        if (nameKeyword == null || nameKeyword.isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符
        nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
        // 构建查询条件
        nameKeyword = "%" + nameKeyword + "%";
        List<TemplateStructurePO> poList = templateStructureDao.selectByNameLike(nameKeyword);
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return TemplateStructureConverter.toEntityList(poList);
    }

    @Override
    public boolean existsByCode(TemplateCode templateCode) {
        if (templateCode == null) {
            return false;
        }

        return templateStructureDao.countByTemplateCode(templateCode.getCode()) > 0;
    }

    @Override
    public boolean existsByCodeAndVersion(TemplateCode templateCode, String version) {
        if (templateCode == null || version == null) {
            return false;
        }
        TemplateStructurePO templateStructurePOReq = new TemplateStructurePO();
        templateStructurePOReq.setTemplateCode(templateCode.getCode());
        templateStructurePOReq.setVersion(version);

        return templateStructureDao.countByTemplateCodeAndVersion(templateStructurePOReq) > 0;
    }

    @Override
    public int deleteById(TemplateId templateId) {
        if (templateId == null) {
            return 0;
        }

        return updateStatus(templateId, Status.DELETED);
    }

    /**
     * 保存时清除相关缓存
     */
    private void clearCacheOnSave(StructureTemplateEntity template) {
        if (template == null || template.getId() == null) {
            return;
        }
        clearRelationCache(template.getId());
    }

    /**
     * 更新时清除相关缓存
     */
    private void clearCacheOnUpdate(StructureTemplateEntity template) {
        if (template == null || template.getId() == null) {
            return;
        }
        redisService.remove(getTemplateByIdCacheKey(template.getId().getId()));
        clearRelationCache(template.getId());
    }

    /**
     * 清除缓存
     *
     * @param templateId 模板ID
     */
    private void clearRelationCache(TemplateId templateId) {
        redisService.remove(getTemplateAllCacheKey());
        // 查找需要清理的其他缓存
        Optional<StructureTemplateEntity> entity = findById(templateId);
        entity.ifPresent(e -> {
            if (e.getTemplateCode() != null) {
                redisService.remove(getTemplateByCodeCacheKey(e.getTemplateCode().getCode()));
                if (e.getVersion() != null) {
                    redisService.remove(getTemplateByCodeAndVersionCacheKey(
                            e.getTemplateCode().getCode(), e.getVersion()));
                }
            }
        });
    }

    private String getTemplateByIdCacheKey(Long id) {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_BY_ID_KEY + id;
    }

    private String getTemplateByCodeCacheKey(String templateCode) {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_BY_CODE_KEY + templateCode;
    }

    private String getTemplateByCodeAndVersionCacheKey(String templateCode, String version) {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_BY_CODE_VERSION_KEY + templateCode + Constants.UNDERLINE + version;
    }

    private String getTemplateAllCacheKey() {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_ALL_KEY;
    }

}
