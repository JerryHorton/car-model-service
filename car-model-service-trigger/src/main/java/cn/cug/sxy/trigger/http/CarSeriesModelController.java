package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.ICarSeriesModelService;
import cn.cug.sxy.api.dto.CarModelCreateRequestDTO;
import cn.cug.sxy.api.dto.CarSeriesCreateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.CarModelVO;
import cn.cug.sxy.api.vo.CarSeriesVO;
import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.valobj.ModelCode;
import cn.cug.sxy.domain.series.model.valobj.PowerType;
import cn.cug.sxy.domain.series.service.ICarModelCommandService;
import cn.cug.sxy.domain.series.service.ICarModelQueryService;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.series.service.ICarSeriesCommandService;
import cn.cug.sxy.domain.series.service.ICarSeriesQueryService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/8 16:35
 * @Description
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/series/")
@DubboService(version = "1.0")
public class CarSeriesModelController implements ICarSeriesModelService {

    private final ICarSeriesQueryService carSeriesQueryService;
    private final ICarModelQueryService carModelQueryService;
    private final ICarSeriesCommandService carSeriesCommandService;
    private final ICarModelCommandService carModelCommandService;

    public CarSeriesModelController(
            ICarSeriesQueryService carSeriesQueryService,
            ICarModelQueryService carModelQueryService,
            ICarSeriesCommandService carSeriesCommandService,
            ICarModelCommandService carModelCommandService) {
        this.carSeriesQueryService = carSeriesQueryService;
        this.carModelQueryService = carModelQueryService;
        this.carSeriesCommandService = carSeriesCommandService;
        this.carModelCommandService = carModelCommandService;
    }

    @RequestMapping(value = "create_series", method = RequestMethod.POST)
    @Override
    public Response<CarSeriesVO> createSeries(@RequestBody @Valid CarSeriesCreateRequestDTO requestDTO) {
        String seriesCode = requestDTO.getSeriesCode();
        String seriesName = requestDTO.getSeriesName();
        String brand = requestDTO.getBrand();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建车系 seriesCode={}, seriesName={}, brand={}, creator={}",
                    seriesCode, seriesName, brand, creator);
            CarSeriesEntity carSeries = carSeriesCommandService.createCarSeries(
                    new SeriesCode(seriesCode),
                    new Brand(brand),
                    seriesName,
                    requestDTO.getDescription());
            log.info("创建车系成功 seriesId={}, seriesCode={}, seriesName={}, brand={}, creator={}",
                    carSeries.getId(), seriesCode, seriesName, brand, creator);
            CarSeriesVO carSeriesVO = convertToCarSeriesVO(carSeries);

            return Response.<CarSeriesVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(carSeriesVO)
                    .build();
        } catch (AppException e) {
            log.error("创建车系失败 seriesCode={}, seriesName={}, brand={}, creator={}",
                    seriesCode, seriesName, brand, creator, e);

            return Response.<CarSeriesVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建车系异常 seriesCode={}, seriesName={}, brand={}, creator={}",
                    seriesCode, seriesName, brand, creator, e);

            return Response.<CarSeriesVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "create_model", method = RequestMethod.POST)
    @Override
    public Response<CarModelVO> createModel(@ModelAttribute @Valid CarModelCreateRequestDTO requestDTO) {
        Long seriesId = requestDTO.getSeriesId();
        String modelCode = requestDTO.getModelCode();
        String modelName = requestDTO.getModelName();
        String powerType = requestDTO.getPowerType();
        try {
            log.info("创建车型 seriesId={}, modelCode={}, modelName={}, powerType={}, creator={}",
                    seriesId, modelCode, modelName, powerType, requestDTO.getCreator());
            CarSeriesEntity seriesEntity = carSeriesQueryService.getById(new SeriesId(seriesId));
            if (seriesEntity == null) {
                throw new AppException("指定的车系不存在");
            }
            CarModelEntity carModel = carModelCommandService.createCarModel(
                    new ModelCode(modelCode),
                    new Brand(requestDTO.getBrand()),
                    PowerType.fromCode(powerType),
                    new SeriesId(seriesId),
                    modelName,
                    null,
                    requestDTO.getDescription()
            );
            log.info("创建车型成功 modelId={}, modelCode={}, modelName={}, powerType={}, creator={}",
                    carModel.getId(), modelCode, modelName, powerType, requestDTO.getCreator());
            CarModelVO carModelVO = convertToCarModelVO(carModel);

            return Response.<CarModelVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(carModelVO)
                    .build();
        } catch (AppException e) {
            log.error("创建车型失败 seriesId={}, modelCode={}, modelName={}, powerType={}, creator={}",
                    seriesId, modelCode, modelName, powerType, requestDTO.getCreator(), e);

            return Response.<CarModelVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建车型异常 seriesId={}, modelCode={}, modelName={}, powerType={}, creator={}",
                    seriesId, modelCode, modelName, powerType, requestDTO.getCreator(), e);

            return Response.<CarModelVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_all_series", method = RequestMethod.GET)
    @Override
    public Response<List<CarSeriesVO>> queryAllSeries() {
        try {
            log.info("查询所有车系");
            List<CarSeriesEntity> seriesEntities = carSeriesQueryService.getAll();
            List<CarSeriesVO> seriesVOs = seriesEntities.stream()
                    .map(this::convertToCarSeriesVO)
                    .collect(Collectors.toList());
            log.info("查询所有车系成功 resultSize={}", seriesVOs.size());
            return Response.<List<CarSeriesVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(seriesVOs)
                    .build();
        } catch (AppException e) {
            log.error("查询所有车系失败", e);

            return Response.<List<CarSeriesVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询所有车系异常", e);

            return Response.<List<CarSeriesVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "model/query_all_models", method = RequestMethod.GET)
    @Override
    public Response<List<CarModelVO>> queryAllModels() {
        try {
            log.info("查询所有车型");
            List<CarModelEntity> modelEntities = carModelQueryService.getAll();
            List<CarModelVO> modelVOs = modelEntities.stream()
                    .map(this::convertToCarModelVO)
                    .collect(Collectors.toList());
            log.info("查询所有车型成功 resultSize={}", modelVOs.size());

            return Response.<List<CarModelVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(modelVOs)
                    .build();
        } catch (AppException e) {
            log.error("查询所有车型失败", e);

            return Response.<List<CarModelVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询所有车型异常", e);

            return Response.<List<CarModelVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "model/query_models_by_series_id", method = RequestMethod.GET)
    @Override
    public Response<List<CarModelVO>> queryModelsBySeriesId(Long seriesId) {
        try {
            if (seriesId == null || seriesId <= 0) {
                throw new AppException("车系ID不能为空或小于等于0");
            }
            log.info("根据车系ID查询车型，seriesId: {}", seriesId);
            List<CarModelEntity> modelEntities = carModelQueryService.getBySeriesId(new SeriesId(seriesId));
            List<CarModelVO> modelVOs = modelEntities.stream()
                    .map(this::convertToCarModelVO)
                    .collect(Collectors.toList());
            log.info("根据车系ID查询车型成功 seriesId={}, resultSize={}", seriesId, modelVOs.size());

            return Response.<List<CarModelVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(modelVOs)
                    .build();
        } catch (AppException e) {
            log.error("根据车系ID查询车型失败 seriesId={}", seriesId, e);
            return Response.<List<CarModelVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("根据车系ID查询车型异常 seriesId={}", seriesId, e);
            return Response.<List<CarModelVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 转换车系实体为VO
     */
    private CarSeriesVO convertToCarSeriesVO(CarSeriesEntity entity) {
        return CarSeriesVO.builder()
                .seriesId(entity.getId().getId())
                .seriesCode(entity.getSeriesCode().getCode())
                .brand(entity.getBrand().getName())
                .seriesName(entity.getSeriesName())
                .description(entity.getDescription())
                .build();
    }

    /**
     * 转换车型实体为VO
     */
    private CarModelVO convertToCarModelVO(CarModelEntity entity) {
        // 获取车系信息
        CarSeriesEntity seriesEntity;
        CarModelVO carModelVO = CarModelVO.builder()
                .modelId(entity.getId().getId())
                .modelCode(entity.getModelCode().getCode())
                .modelName(entity.getModelName())
                .powerTypeCode(entity.getPowerType().getCode())
                .powerTypeDesc(entity.getPowerType().getInfo())
                .statusCode(entity.getStatus().getCode())
                .statusDesc(entity.getStatus().getInfo())
                .seriesId(entity.getSeriesId().getId())
                .iconPath(entity.getIconPath())
                .description(entity.getDescription())
                .build();
        try {
            seriesEntity = carSeriesQueryService.getById(entity.getSeriesId());
            if (seriesEntity != null) {
                carModelVO.setBrand(seriesEntity.getBrand().getName());
                carModelVO.setSeriesName(seriesEntity.getSeriesName());
            }
        } catch (Exception e) {
            log.warn("获取车系信息失败，seriesId: {}", entity.getSeriesId().getId(), e);
        }

        return carModelVO;
    }

}
