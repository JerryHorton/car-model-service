package cn.cug.sxy.domain.model.service;

import cn.cug.sxy.domain.model.adapter.repository.ICarModelRepository;
import cn.cug.sxy.domain.model.model.entity.CarModelEntity;
import cn.cug.sxy.domain.model.model.valobj.ModelCode;
import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.model.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/22 09:23
 * @Description 车型命令服务实现
 * @Author jerryhotton
 */

@Service
public class CarModelCommandService implements ICarModelCommandService {

    private final Logger logger = LoggerFactory.getLogger(CarModelCommandService.class);

    private final ICarModelRepository carModelRepository;

    public CarModelCommandService(ICarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
    }

    @Override
    public CarModelEntity createCarModel(ModelCode modelCode, Brand brand, PowerType powerType, SeriesId seriesId, String modelName, String iconPath, String description) {
        logger.info("创建车型，modelCode={}, brand={}, powerType={}, seriesId={}, modelName={}, iconPath={}, description={}", modelCode, brand, powerType, seriesId, modelName, iconPath, description);
        if (carModelRepository.existsByCode(modelCode)) {
            throw new IllegalArgumentException("车型编码已存在");
        }
        CarModelEntity carModelEntity = CarModelEntity.create(
                modelCode,
                brand,
                powerType,
                seriesId,
                modelName,
                iconPath,
                description
        );
        carModelRepository.save(carModelEntity);
        logger.info("车型创建成功，modelId={}", carModelEntity.getModelId());

        return carModelEntity;
    }

    @Override
    public int updateCarModel(CarModelEntity carModelEntity) {
        return carModelRepository.update(carModelEntity);
    }

    @Override
    public CarModelEntity publishCarModel(ModelId modelId) {
        logger.info("发布车型，modelId={}", modelId);
        Optional<CarModelEntity> carModelEntityOpt = carModelRepository.findById(modelId);
        if (!carModelEntityOpt.isPresent()) {
            logger.error("车型不存在，modelId={}", modelId);
            throw new AppException(ResponseCode.CAR_MODEL_NOT_EXIST_ERROR);
        }
        CarModelEntity carModelEntity = carModelEntityOpt.get();
        carModelEntity.publish();
        carModelRepository.update(carModelEntity);
        logger.info("车型发布成功，modelId={}", modelId);

        return carModelEntity;
    }

    @Override
    public CarModelEntity deprecateCarModel(ModelId modelId) {
        logger.info("废止车型，modelId={}", modelId);
        Optional<CarModelEntity> carModelEntityOpt = carModelRepository.findById(modelId);
        if (!carModelEntityOpt.isPresent()) {
            logger.error("车型不存在，modelId={}", modelId);
            throw new AppException(ResponseCode.CAR_MODEL_NOT_EXIST_ERROR);
        }
        CarModelEntity carModelEntity = carModelEntityOpt.get();
        carModelEntity.deprecate();
        carModelRepository.update(carModelEntity);
        logger.info("车型废止成功，modelId={}", modelId);

        return carModelEntity;
    }

    @Override
    public boolean removeCarModel(ModelId modelId) {
        logger.info("删除车型，modelId={}", modelId);
        return carModelRepository.remove(modelId);
    }

}
