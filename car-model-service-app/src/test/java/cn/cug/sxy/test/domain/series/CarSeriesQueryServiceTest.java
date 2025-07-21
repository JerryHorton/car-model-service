package cn.cug.sxy.test.domain.series;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.service.ICarSeriesQueryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/21 16:56
 * @Description
 * @Author jerryhotton
 */

@Slf4j
@SpringBootTest
public class CarSeriesQueryServiceTest {

    @Resource
    private ICarSeriesQueryService carSeriesQueryService;

    @Test
    public void test_getAll() {
        List<CarSeriesEntity> carSeriesEntityList = carSeriesQueryService.getAll();
        log.info("carSeriesEntityList:{}", carSeriesEntityList);
    }

    @Test
    public void test_getBySeriesNameLike() {
        String seriesName = "测试系列";
        List<CarSeriesEntity> carSeriesEntityList = carSeriesQueryService.getBySeriesNameLike(seriesName);
        log.info("carSeriesEntityList:{}", carSeriesEntityList);
    }

}
