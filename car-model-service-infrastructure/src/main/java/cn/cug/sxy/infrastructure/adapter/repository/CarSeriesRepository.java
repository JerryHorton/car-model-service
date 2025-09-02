package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.series.adapter.repository.ICarSeriesRepository;
import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.*;
import cn.cug.sxy.infrastructure.converter.CarModelConverter;
import cn.cug.sxy.infrastructure.converter.CarSeriesConverter;
import cn.cug.sxy.infrastructure.dao.ICarModelDao;
import cn.cug.sxy.infrastructure.dao.ICarSeriesDao;
import cn.cug.sxy.infrastructure.dao.po.CarModelPO;
import cn.cug.sxy.infrastructure.dao.po.CarSeriesPO;
import cn.cug.sxy.infrastructure.minio.IFileStorageService;
import cn.cug.sxy.types.common.Constants;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Date 2025/7/21 15:33
 * @Description 车型系列仓储实现
 * @Author jerryhotton
 */

@Repository
public class CarSeriesRepository extends AbstractRepository implements ICarSeriesRepository {

    private final ICarSeriesDao carSeriesDao;
    private final ICarModelDao carModelDao;
    private final IFileStorageService fileStorageService;

    public CarSeriesRepository(
            ICarSeriesDao carSeriesDao,
            ICarModelDao carModelDao,
            IFileStorageService fileStorageService) {
        this.carSeriesDao = carSeriesDao;
        this.carModelDao = carModelDao;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public CarSeriesEntity save(CarSeriesEntity carSeriesEntity) {
        CarSeriesPO carSeriesPO = CarSeriesConverter.toPO(carSeriesEntity);
        carSeriesDao.insert(carSeriesPO);
        if (carSeriesPO.getId() != null && carSeriesEntity.getId() == null) {
            carSeriesEntity.setId(new SeriesId(carSeriesPO.getId()));
        }

        return carSeriesEntity;
    }

    @Override
    public boolean remove(SeriesId seriesId) {
        if (seriesId == null) {
            return false;
        }
        int count = carSeriesDao.deleteBySeriesId(seriesId.getId());

        return count == 1;
    }

    @Override
    public Optional<CarSeriesEntity> findById(SeriesId seriesId) {
        if (seriesId == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.CAR_SERIES_BY_ID_KEY + seriesId.getId();
        CarSeriesPO carSeriesPO = getDataFromCacheOrDB(cacheKey, () -> carSeriesDao.selectById(seriesId.getId()));
        if (carSeriesPO == null) {
            return Optional.empty();
        }

        return Optional.of(CarSeriesConverter.toEntity(carSeriesPO));
    }

    @Override
    public Optional<CarSeriesEntity> findByCode(SeriesCode seriesCode) {
        if (seriesCode == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.CAR_SERIES_BY_CODE_KEY + seriesCode.getCode();
        CarSeriesPO carSeriesPO = getDataFromCacheOrDB(cacheKey, () -> carSeriesDao.selectBySeriesCode(seriesCode.getCode()));
        if (null == carSeriesPO) {
            return Optional.empty();
        }

        return Optional.of(CarSeriesConverter.toEntity(carSeriesPO));
    }

    @Override
    public List<CarSeriesEntity> findSeriesByBrand(Brand brand) {
        if (brand == null) {
            return Collections.emptyList();
        }
        String cacheKey = Constants.RedisKey.CAR_SERIES_BY_BRAND_KEY + brand.getName();
        List<CarSeriesPO> carSeriesPOList = getDataFromCacheOrDB(cacheKey, () -> carSeriesDao.selectBySeriesBrand(brand.getName()));
        if (carSeriesPOList == null || carSeriesPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarSeriesConverter.toEntityList(carSeriesPOList);
    }

    @Override
    public List<CarSeriesEntity> findBySeriesNameLike(String seriesName) {
        if (null == seriesName || seriesName.isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符，防止意外的通配符行为
        seriesName = seriesName.replace("%", "\\%").replace("_", "\\_");
        String seriesNameLike = "%" + seriesName + "%";
        List<CarSeriesPO> carSeriesPOList = carSeriesDao.selectBySeriesNameLike(seriesNameLike);
        if (null == carSeriesPOList || carSeriesPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarSeriesConverter.toEntityList(carSeriesPOList);
    }

    @Override
    public List<CarSeriesEntity> findAll() {
        List<CarSeriesPO> carSeriesPOList = carSeriesDao.selectAll();
        if (null == carSeriesPOList || carSeriesPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarSeriesConverter.toEntityList(carSeriesPOList);
    }

    @Override
    public boolean existsByCode(SeriesCode seriesCode) {
        CarSeriesPO carSeriesPO = carSeriesDao.selectBySeriesCode(seriesCode.getCode());

        return carSeriesPO != null;
    }

    @Override
    public int update(CarSeriesEntity carSeriesEntity) {
        CarSeriesPO carSeriesPO = CarSeriesConverter.toPO(carSeriesEntity);
        return carSeriesDao.update(carSeriesPO);
    }

    @Override
    public void save(CarModelEntity carModelEntity) {
        CarModelPO po = CarModelConverter.toPO(carModelEntity);
        carModelDao.insert(po);
        if (po.getId() != null && carModelEntity.getId() == null) {
            carModelEntity.setId(new ModelId(po.getId()));
        }
    }

    @Override
    public String uploadModelIcon(String fileData, String fileName, String contentType, Long modelId) {
        return fileStorageService.uploadModelIcon(fileData, fileName, contentType, modelId);
    }

    @Override
    public int update(CarModelEntity carModelEntity) {
        return carModelDao.update(CarModelConverter.toPO(carModelEntity));
    }

    @Override
    public int updateModelIconPath(CarModelEntity modelEntity) {
        String filePath = modelEntity.getIconPath();
        String downloadUrl = fileStorageService.generatePresignedUrl(filePath, 7, TimeUnit.DAYS);
        modelEntity.setDownloadUrl(downloadUrl);

        return carModelDao.update(CarModelConverter.toPO(modelEntity));
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
    public List<CarModelEntity> findModelByBrand(Brand brand) {
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
    public List<CarModelEntity> findAllModel() {
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
    public boolean deleteModelIcon(String iconPath) {
        return fileStorageService.delete(iconPath);
    }

    @Override
    public boolean existsByCode(ModelCode modelCode) {
        return carModelDao.countByModelCode(modelCode.getCode()) > 0;
    }


}
