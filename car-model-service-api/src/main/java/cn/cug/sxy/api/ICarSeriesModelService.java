package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.CarModelCreateRequestDTO;
import cn.cug.sxy.api.dto.CarSeriesCreateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.CarModelVO;
import cn.cug.sxy.api.vo.CarSeriesVO;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/8 16:31
 * @Description 车系车型服务接口
 * @Author jerryhotton
 */

public interface ICarSeriesModelService {

    /**
     * 创建车系
     *
     * @param requestDTO 创建车系请求DTO
     * @return 创建结果
     */
    Response<CarSeriesVO> createSeries(CarSeriesCreateRequestDTO requestDTO);

    /**
     * 创建车型
     *
     * @param requestDTO 创建车型请求DTO
     * @return 创建结果
     */
    Response<CarModelVO> createModel(CarModelCreateRequestDTO requestDTO);

    /**
     * 查询所有车系
     *
     * @return 车系列表
     */
    Response<List<CarSeriesVO>> queryAllSeries();

    /**
     * 查询所有车型
     *
     * @return 车型列表
     */
    Response<List<CarModelVO>> queryAllModels();

    /**
     * 根据车系ID查询车型
     *
     * @param seriesId 车系ID
     * @return 车型列表
     */
    Response<List<CarModelVO>> queryModelsBySeriesId(Long seriesId);

}
