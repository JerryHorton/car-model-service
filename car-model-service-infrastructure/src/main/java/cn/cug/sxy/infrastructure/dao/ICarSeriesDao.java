package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.CarSeries;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/21 15:05
 * @Description 车系数据访问层接口
 * @Author jerryhotton
 */

@Mapper
public interface ICarSeriesDao {

    void insert(CarSeries carSeries);

    int deleteBySeriesId(String seriesId);

    CarSeries selectBySeriesId(String seriesId);

    CarSeries selectBySeriesCode(String seriesCode);

    List<CarSeries> selectBySeriesBrand(String seriesBrand);

    List<CarSeries> selectBySeriesNameLike(String seriesNameLike);

    List<CarSeries> selectAll();

}
