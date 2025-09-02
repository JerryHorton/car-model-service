package cn.cug.sxy.test.domain.series;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.series.service.ICarSeriesCommandService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @version 1.0
 * @Date 2025/7/21 16:39
 * @Description 车型系列命令服务测试类
 * @Author jerryhotton
 */

@Slf4j
@SpringBootTest
public class CarSeriesCommandServiceTest {

    @Resource
    private ICarSeriesCommandService carSeriesCommandService;

    @Test
    public void test_createCarSeries() {
        CarSeriesEntity carSeriesEntity = carSeriesCommandService.createCarSeries(
                new SeriesCode("ADW28sW2"),
                new Brand("test"),
                "测试系列002",
                "test"
        );
        log.info("测试成功 carSeriesEntity:{}", carSeriesEntity);
    }

    @Test
    public void test_removeCarSeries() {
        boolean result = carSeriesCommandService.removeCarSeries(new SeriesId(1L));
        log.info("测试成功 result:{}", result);
    }

}
