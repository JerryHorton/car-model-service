package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.model.adapter.repository.ICarModelRepository;
import cn.cug.sxy.domain.model.model.entity.CarModelEntity;
import cn.cug.sxy.domain.model.model.valobj.ModelCode;
import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.model.model.valobj.ModelStatus;
import cn.cug.sxy.domain.model.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.infrastructure.converter.CarModelConverter;
import cn.cug.sxy.infrastructure.dao.ICarModelDao;
import cn.cug.sxy.infrastructure.dao.po.CarModelPO;
import cn.cug.sxy.types.common.Constants;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/21 19:21
 * @Description 车型仓储实现
 * @Author jerryhotton
 */

@Repository
public class CarModelRepository extends AbstractRepository implements ICarModelRepository {

    private final ICarModelDao carModelDao;

    public CarModelRepository(ICarModelDao carModelDao) {
        this.carModelDao = carModelDao;
    }

    @Override
    public void save(CarModelEntity carModelEntity) {
        carModelDao.insert(CarModelConverter.toPO(carModelEntity));
    }

    @Override
    public int update(CarModelEntity carModelEntity) {
        return carModelDao.update(CarModelConverter.toPO(carModelEntity));
    }

    @Override
    public Optional<CarModelEntity> findById(ModelId modelId) {
        if (modelId == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.CAR_MODEL_BY_ID_KEY + modelId.getId();
        CarModelPO carModelPO = getDataFromCacheOrDB(cacheKey, () -> carModelDao.selectByModelId(modelId.getId()));
        if (carModelPO == null) {
            return Optional.empty();
        }

        return Optional.of(CarModelConverter.toEntity(carModelPO));
    }

    @Override
    public Optional<CarModelEntity> findByCode(ModelCode modelCode) {
        if (modelCode == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.CAR_MODEL_BY_CODE_KEY + modelCode.getCode();
        CarModelPO carModelPO = getDataFromCacheOrDB(cacheKey, () -> carModelDao.selectByModelCode(modelCode.getCode()));
        if (carModelPO == null) {
            return Optional.empty();
        }

        return Optional.of(CarModelConverter.toEntity(carModelPO));
    }

    @Override
    public List<CarModelEntity> findByBrand(Brand brand) {
        if (brand == null) {
            return Collections.emptyList();
        }
        String cacheKey = Constants.RedisKey.CAR_MODEL_BY_BRAND_KEY + brand.getName();
        List<CarModelPO> carModelPOList = getDataFromCacheOrDB(cacheKey, () -> carModelDao.selectByBrand(brand.getName()));
        if (carModelPOList == null || carModelPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarModelConverter.toEntityList(carModelPOList);
    }

    @Override
    public List<CarModelEntity> findByPowerType(PowerType powerType) {
        if (powerType == null) {
            return Collections.emptyList();
        }
        String cacheKey = Constants.RedisKey.CAR_MODEL_BY_POWER_TYPE_KEY + powerType.getCode();
        List<CarModelPO> carModelPOList = getDataFromCacheOrDB(cacheKey, () -> carModelDao.selectByPowerType(powerType.getCode()));
        if (carModelPOList == null || carModelPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarModelConverter.toEntityList(carModelPOList);
    }

    @Override
    public List<CarModelEntity> findByStatus(ModelStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<CarModelPO> carModelPOList = carModelDao.selectByStatus(status.getCode());
        if (carModelPOList == null || carModelPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarModelConverter.toEntityList(carModelPOList);
    }

    @Override
    public List<CarModelEntity> findBySeriesId(SeriesId seriesId) {
        if (seriesId == null) {
            return Collections.emptyList();
        }
        String cacheKey = Constants.RedisKey.CAR_MODEL_BY_SERIES_ID_KEY + seriesId.getId();
        List<CarModelPO> carModelPOList = getDataFromCacheOrDB(cacheKey, () -> carModelDao.selectBySeriesId(seriesId.getId()));
        if (carModelPOList == null || carModelPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarModelConverter.toEntityList(carModelPOList);
    }

    @Override
    public List<CarModelEntity> findByModelNameLike(String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符，防止意外的通配符行为
        modelName = modelName.replace("%", "\\%").replace("_", "\\_");
        String modelNameLike = "%" + modelName + "%";
        List<CarModelPO> carModelPOList = carModelDao.selectByModelNameLike(modelNameLike);
        if (null == carModelPOList || carModelPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarModelConverter.toEntityList(carModelPOList);
    }

    @Override
    public List<CarModelEntity> findAll() {
        List<CarModelPO> carModelPOList = carModelDao.selectAll();
        if (null == carModelPOList || carModelPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarModelConverter.toEntityList(carModelPOList);
    }

    @Override
    public boolean remove(ModelId modelId) {
        return carModelDao.deleteByModelId(modelId.getId()) == 1;
    }

    @Override
    public boolean existsByCode(ModelCode modelCode) {
        return carModelDao.countByModelCode(modelCode.getCode()) > 0;
    }

}
