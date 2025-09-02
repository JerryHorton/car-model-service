package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.CarSeriesPO;
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

    void insert(CarSeriesPO carSeriesPO);

    int deleteBySeriesId(Long seriesId);

    CarSeriesPO selectById(Long seriesId);

    CarSeriesPO selectBySeriesCode(String seriesCode);

    List<CarSeriesPO> selectBySeriesBrand(String seriesBrand);

    List<CarSeriesPO> selectBySeriesNameLike(String seriesNameLike);

    List<CarSeriesPO> selectAll();

    int update(CarSeriesPO carSeriesPO);

}
