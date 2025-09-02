package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.adapter.repository.ICarModelRepository;
import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.valobj.ModelCode;
import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
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

    private final ICarModelRepository carModelRepository;

    public CarModelCommandService(ICarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
    }

    @Override
    public CarModelEntity createCarModel(ModelCode modelCode, Brand brand, PowerType powerType, SeriesId seriesId, String modelName, String iconPath, String description) {
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

        return carModelEntity;
    }

    @Override
    public String uploadModelIcon(String fileData, String fileName, String contentType, Long modelId) {
        return carModelRepository.uploadModelIcon(fileData, fileName, contentType, modelId);
    }

    @Override
    public boolean deleteModelIcon(String iconPath) {
        return carModelRepository.deleteModelIcon(iconPath);
    }

    @Override
    public int updateModelIconPath(CarModelEntity modelEntity) {
        return carModelRepository.updateModelIconPath(modelEntity);
    }

    @Override
    public int updateCarModel(CarModelEntity carModelEntity) {
        return carModelRepository.update(carModelEntity);
    }

    @Override
    public CarModelEntity publishCarModel(ModelId modelId) {
        Optional<CarModelEntity> carModelEntityOpt = carModelRepository.findById(modelId);
        if (!carModelEntityOpt.isPresent()) {
            throw new AppException(ResponseCode.CAR_MODEL_NOT_EXIST_ERROR);
        }
        CarModelEntity carModelEntity = carModelEntityOpt.get();
        carModelEntity.publish();
        carModelRepository.update(carModelEntity);

        return carModelEntity;
    }

    @Override
    public CarModelEntity deprecateCarModel(ModelId modelId) {
        Optional<CarModelEntity> carModelEntityOpt = carModelRepository.findById(modelId);
        if (!carModelEntityOpt.isPresent()) {
            throw new AppException(ResponseCode.CAR_MODEL_NOT_EXIST_ERROR);
        }
        CarModelEntity carModelEntity = carModelEntityOpt.get();
        carModelEntity.deprecate();
        carModelRepository.update(carModelEntity);

        return carModelEntity;
    }

    @Override
    public boolean removeCarModel(ModelId modelId) {
        return carModelRepository.remove(modelId);
    }

}
