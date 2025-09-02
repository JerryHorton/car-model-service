package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.model.valobj.VinCode;
import cn.cug.sxy.domain.series.model.valobj.VinQueryResult;

/**
 * @version 1.0
 * @Date 2025/7/22 17:24
 * @Description VIN码查询服务接口
 * @Author jerryhotton
 */

public interface IVinQueryService {

    /**
     * 根据VIN码查询车型信息
     *
     * @param vinCode VIN码
     * @return 查询结果
     */
    VinQueryResult queryByVin(VinCode vinCode);

}
