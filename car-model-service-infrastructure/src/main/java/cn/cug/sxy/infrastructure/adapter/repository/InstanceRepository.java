package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.adapter.repository.IInstanceRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.domain.structure.model.valobj.InstanceCode;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.domain.structure.model.valobj.StructureInstancePageVO;
import cn.cug.sxy.infrastructure.converter.InstanceStructureConverter;
import cn.cug.sxy.infrastructure.dao.IInstanceStructureDao;
import cn.cug.sxy.infrastructure.dao.po.InstanceStructurePO;
import cn.cug.sxy.infrastructure.redis.IRedisService;
import cn.cug.sxy.types.common.Constants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/30 17:46
 * @Description 车型结构树实例仓储实现
 * @Author jerryhotton
 */

@Repository
public class InstanceRepository extends AbstractRepository implements IInstanceRepository {

    private final IInstanceStructureDao instanceStructureDao;
    private final IRedisService redisService;

    public InstanceRepository(
            IInstanceStructureDao instanceStructureDao,
            IRedisService redisService) {
        this.instanceStructureDao = instanceStructureDao;
        this.redisService = redisService;
    }

    @Override
    public StructureInstanceEntity save(StructureInstanceEntity instance) {
        if (instance == null) {
            return null;
        }
        InstanceStructurePO po = InstanceStructureConverter.toPO(instance);
        instanceStructureDao.insert(po);
        // 设置ID
        if (po.getId() != null && instance.getId() == null) {
            instance.setId(new InstanceId(po.getId()));
        }
        clearCacheOnSave(instance);

        return instance;
    }

    @Override
    public int update(StructureInstanceEntity instance) {
        if (instance == null || instance.getId() == null) {
            return 0;
        }
        // 清除缓存
        clearCacheOnUpdate(instance);
        InstanceStructurePO po = InstanceStructureConverter.toPO(instance);

        return instanceStructureDao.update(po);
    }

    @Override
    public int updateStatus(InstanceId instanceId, Status status) {
        if (instanceId == null || status == null) {
            return 0;
        }
        InstanceStructurePO instanceStructurePOReq = new InstanceStructurePO();
        instanceStructurePOReq.setId(instanceId.getId());
        instanceStructurePOReq.setStatus(status.getCode());
        int result = instanceStructureDao.updateStatus(instanceStructurePOReq);
        // 更新缓存
        if (result > 0) {
            redisService.remove(getInstanceByIdCacheKey(instanceId.getId()));
            clearRelationCache(instanceId);
        }

        return result;
    }

    @Override
    public int updatePublishStatus(InstanceId instanceId, boolean isPublished) {
        if (instanceId == null) {
            return 0;
        }
        InstanceStructurePO instanceStructurePOReq = new InstanceStructurePO();
        instanceStructurePOReq.setId(instanceId.getId());
        instanceStructurePOReq.setIsPublished(isPublished);
        int result = instanceStructureDao.updatePublishStatus(instanceStructurePOReq);
        // 清除缓存
        if (result > 0) {
            redisService.remove(getInstanceByIdCacheKey(instanceId.getId()));
            clearRelationCache(instanceId);
        }

        return result;
    }

    @Override
    public Optional<StructureInstanceEntity> findById(InstanceId instanceId) {
        if (instanceId == null) {
            return Optional.empty();
        }
        InstanceStructurePO instanceStructurePO = getDataFromCacheOrDB(getInstanceByIdCacheKey(instanceId.getId()), () ->
                instanceStructureDao.selectById(instanceId.getId()));
        if (instanceStructurePO == null) {
            return Optional.empty();
        }
        StructureInstanceEntity entity = InstanceStructureConverter.toEntity(instanceStructurePO);

        return Optional.of(entity);
    }

    @Override
    public List<StructureInstanceEntity> findByInstanceCode(InstanceCode instanceCode) {
        if (instanceCode == null) {
            return Collections.emptyList();
        }
        List<InstanceStructurePO> instanceStructurePOList = getDataFromCacheOrDB(getInstanceByCodeCacheKey(instanceCode.getCode()), () ->
                instanceStructureDao.selectByInstanceCode(instanceCode.getCode()));
        if (instanceStructurePOList == null || instanceStructurePOList.isEmpty()) {
            return Collections.emptyList();
        }

        return InstanceStructureConverter.toEntityList(instanceStructurePOList);
    }

    @Override
    public Optional<StructureInstanceEntity> findByInstanceCodeAndVersion(InstanceCode instanceCode, String version) {
        if (instanceCode == null || version == null) {
            return Optional.empty();
        }
        InstanceStructurePO instanceStructurePOReq = new InstanceStructurePO();
        instanceStructurePOReq.setInstanceCode(instanceCode.getCode());
        instanceStructurePOReq.setVersion(version);
        InstanceStructurePO instanceStructurePO = getDataFromCacheOrDB(getInstanceByCodeAndVersionCacheKey(instanceCode.getCode(), version), () ->
                instanceStructureDao.selectByInstanceCodeAndVersion(instanceStructurePOReq));
        if (instanceStructurePO == null) {
            return Optional.empty();
        }
        StructureInstanceEntity entity = InstanceStructureConverter.toEntity(instanceStructurePO);

        return Optional.of(entity);
    }

    @Override
    public List<StructureInstanceEntity> findBySeriesId(SeriesId seriesId) {
        if (seriesId == null) {
            return Collections.emptyList();
        }
        List<InstanceStructurePO> poList = getDataFromCacheOrDB(getInstanceBySeriesIdCacheKey(seriesId.getId()), () ->
                instanceStructureDao.selectBySeriesId(seriesId.getId()));
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return InstanceStructureConverter.toEntityList(poList);
    }

    @Override
    public List<StructureInstanceEntity> findByModelId(ModelId modelId) {
        if (modelId == null) {
            return Collections.emptyList();
        }
        List<InstanceStructurePO> poList = instanceStructureDao.selectByModelId(modelId.getId());
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return InstanceStructureConverter.toEntityList(poList);
    }

    @Override
    public List<StructureInstanceEntity> findByStatus(Status status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<InstanceStructurePO> poList = instanceStructureDao.selectByStatus(status.getCode());
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return InstanceStructureConverter.toEntityList(poList);
    }

    @Override
    public StructureInstancePageVO findInstances(InstanceCode instanceCode, String nameKeyword, Status status, SeriesId seriesId, ModelId modelId, int pageNo, int pageSize) {
        // 启用分页
        PageHelper.startPage(pageNo, pageSize);
        // 查询
        if (nameKeyword != null) {
            nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
            nameKeyword = "%" + nameKeyword + "%";
        }
        InstanceStructurePO instanceStructurePOReq = new InstanceStructurePO();
        instanceStructurePOReq.setInstanceCode(instanceCode == null ? null : instanceCode.getCode());
        instanceStructurePOReq.setInstanceName(nameKeyword);
        instanceStructurePOReq.setStatus(status == null ? null : status.getCode());
        instanceStructurePOReq.setSeriesId(seriesId == null ? null : seriesId.getId());
        instanceStructurePOReq.setModelId(modelId == null ? null : modelId.getId());
        List<InstanceStructurePO> poList = instanceStructureDao.selectByCondition(instanceStructurePOReq);
        // 封装分页
        PageInfo<InstanceStructurePO> pageInfo = new PageInfo<>(poList);

        return StructureInstancePageVO.builder()
                .currentPage(pageInfo.getPageNum())
                .totalPages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .instances(InstanceStructureConverter.toEntityList(pageInfo.getList()))
                .build();
    }

    @Override
    public List<StructureInstanceEntity> findAll() {
        List<InstanceStructurePO> poList = getDataFromCacheOrDB(getInstanceAllCacheKey(), instanceStructureDao::selectAll);
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return InstanceStructureConverter.toEntityList(poList);
    }

    @Override
    public int deleteById(InstanceId instanceId) {
        if (instanceId == null) {
            return 0;
        }

        return updateStatus(instanceId, Status.DELETED);
    }

    @Override
    public boolean existsByCode(InstanceCode instanceCode) {
        if (instanceCode == null) {
            return false;
        }

        return instanceStructureDao.countByInstanceCode(instanceCode.getCode()) > 0;
    }

    @Override
    public boolean existsByCodeAndVersion(InstanceCode instanceCode, String version) {
        if (instanceCode == null || version == null) {
            return false;
        }
        InstanceStructurePO instanceStructurePOReq = new InstanceStructurePO();
        instanceStructurePOReq.setInstanceCode(instanceCode.getCode());
        instanceStructurePOReq.setVersion(version);

        return instanceStructureDao.countByInstanceCodeAndVersion(instanceStructurePOReq) > 0;
    }

    /**
     * 保存时清除相关缓存
     */
    private void clearCacheOnSave(StructureInstanceEntity instance) {
        if (instance == null) {
            return;
        }
        clearRelationCache(instance.getId());
    }

    /**
     * 更新时清除相关缓存
     */
    private void clearCacheOnUpdate(StructureInstanceEntity instance) {
        if (instance == null || instance.getId() == null) {
            return;
        }
        redisService.remove(getInstanceByIdCacheKey(instance.getId().getId()));
        clearRelationCache(instance.getId());
    }

    private void clearRelationCache(InstanceId instanceId) {
        redisService.remove(getInstanceAllCacheKey());
        // 查找需要清理的其他缓存
        Optional<StructureInstanceEntity> entity = findById(instanceId);
        entity.ifPresent(e -> {
            if (e.getSeriesId() != null) {
                redisService.remove(getInstanceBySeriesIdCacheKey(e.getSeriesId().getId()));
            }
            if (e.getModelId() != null) {
                redisService.remove(getInstanceByModelIdCacheKey(e.getModelId().getId()));
            }
            if (e.getInstanceCode() != null) {
                redisService.remove(getInstanceByCodeCacheKey(e.getInstanceCode().getCode()));
                if (e.getVersion() != null) {
                    redisService.remove(getInstanceByCodeAndVersionCacheKey(
                            e.getInstanceCode().getCode(), e.getVersion()));
                }
            }
        });
    }

    private String getInstanceByIdCacheKey(Long id) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_BY_ID_KEY + id;
    }

    private String getInstanceByCodeCacheKey(String instanceCode) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_BY_CODE_KEY + instanceCode;
    }

    private String getInstanceByCodeAndVersionCacheKey(String instanceCode, String version) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_BY_CODE_VERSION_KEY + instanceCode + Constants.UNDERLINE + version;
    }

    private String getInstanceAllCacheKey() {
        return Constants.RedisKey.INSTANCE_STRUCTURE_ALL_KEY;
    }

    private String getInstanceBySeriesIdCacheKey(Long id) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_BY_SERIES_ID_KEY + id;
    }

    private String getInstanceByModelIdCacheKey(Long id) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_BY_MODEL_ID_KEY + id;
    }

}
