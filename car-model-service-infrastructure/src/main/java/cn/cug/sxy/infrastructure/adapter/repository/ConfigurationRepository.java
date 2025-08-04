package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.structure.adapter.repository.IConfigurationRepository;
import cn.cug.sxy.domain.structure.model.entity.ConfigurationEntity;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationCode;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationId;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.infrastructure.converter.ConfigurationConverter;
import cn.cug.sxy.infrastructure.dao.IConfigurationDao;
import cn.cug.sxy.infrastructure.dao.po.ConfigurationPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/4 16:30
 * @Description 车型配置仓储实现
 * @Author jerryhotton
 */

@Repository
public class ConfigurationRepository implements IConfigurationRepository {

    private final IConfigurationDao configurationDao;

    public ConfigurationRepository(IConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
    }

    @Override
    public void save(ConfigurationEntity configuration) {
        if (configuration == null) {
            return;
        }
        ConfigurationPO po = ConfigurationConverter.toPO(configuration);
        configurationDao.insert(po);
        // 为插入后的实体设置ID
        if (po.getId() != null && configuration.getId() == null) {
            configuration.setId(new ConfigurationId(po.getId()));
        }
    }

    @Override
    public int saveBatch(List<ConfigurationEntity> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            return 0;
        }
        List<ConfigurationPO> pos = ConfigurationConverter.toPOList(configurations);

        return configurationDao.insertBatch(pos);
    }

    @Override
    public int update(ConfigurationEntity configuration) {
        if (configuration == null || configuration.getId() == null) {
            return 0;
        }
        ConfigurationPO po = ConfigurationConverter.toPO(configuration);

        return configurationDao.update(po);
    }

    @Override
    public int updateStatus(ConfigurationId configId, Status status) {
        if (configId == null || status == null) {
            return 0;
        }
        ConfigurationPO po = new ConfigurationPO();
        po.setId(configId.getId());
        po.setStatus(status.getCode());

        return configurationDao.updateStatus(po);
    }

    @Override
    public Optional<ConfigurationEntity> findById(ConfigurationId configId) {
        if (configId == null) {
            return Optional.empty();
        }
        ConfigurationPO po = configurationDao.selectById(configId.getId());
        if (po == null) {
            return Optional.empty();
        }
        ConfigurationEntity entity = ConfigurationConverter.toEntity(po);
        entity.setId(configId);

        return Optional.of(entity);
    }

    @Override
    public Optional<ConfigurationEntity> findByCode(ConfigurationCode configCode) {
        if (configCode == null) {
            return Optional.empty();
        }
        ConfigurationPO po = configurationDao.selectByCode(configCode.getCode());
        if (po == null) {
            return Optional.empty();
        }
        ConfigurationEntity entity = ConfigurationConverter.toEntity(po);

        return Optional.of(entity);
    }

    @Override
    public List<ConfigurationEntity> findByNameLike(String nameKeyword) {
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return List.of();
        }
        nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
        nameKeyword = "%" + nameKeyword + "%";
        List<ConfigurationPO> pos = configurationDao.selectByNameLike(nameKeyword);
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigurationConverter.toEntityList(pos);
    }

    @Override
    public List<ConfigurationEntity> findByStatus(Status status) {
        if (status == null) {
            return List.of();
        }
        List<ConfigurationPO> pos = configurationDao.selectByStatus(status.getCode());
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigurationConverter.toEntityList(pos);
    }

    @Override
    public List<ConfigurationEntity> findAllEnabled() {
        List<ConfigurationPO> pos = configurationDao.selectAllEnabled();
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigurationConverter.toEntityList(pos);
    }

    @Override
    public boolean existsByCode(ConfigurationCode configCode) {
        if (configCode == null) {
            return false;
        }

        return configurationDao.countByCode(configCode.getCode()) > 0;
    }

    @Override
    public boolean existsByName(String configName) {
        if (StringUtils.isBlank(configName)) {
            return false;
        }

        return configurationDao.countByName(configName.trim()) > 0;
    }

    @Override
    public int deleteById(ConfigurationId configId) {
        if (configId == null) {
            return 0;
        }

        return configurationDao.deleteById(configId.getId());
    }

    @Override
    public int count() {
        return configurationDao.count();
    }

    @Override
    public int countByStatus(Status status) {
        if (status == null) {
            return 0;
        }

        return configurationDao.countByStatus(status.getCode());
    }

    @Override
    public List<ConfigurationEntity> findWithPagination(ConfigurationCode configCode, String configName, Status status) {
       ConfigurationPO po = new ConfigurationPO();
        po.setConfigCode(configCode.getCode());
        po.setConfigName(configName);
        po.setStatus(status.getCode());
        List<ConfigurationPO> pos = configurationDao.selectByCondition(po);
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigurationConverter.toEntityList(pos);
    }

}
