package cn.cug.sxy.domain.series.adapter.port;

import cn.cug.sxy.domain.series.model.valobj.VinCode;
import cn.cug.sxy.domain.series.model.valobj.VinQueryResult;

/**
 * @version 1.0
 * @Date 2025/7/22 17:16
 * @Description VIN码查询适配端口
 * @Author jerryhotton
 */

public interface IVinQueryPort {

    /**
     * 获取适配器名称
     *
     * @return 适配器名称
     */
    String getName();

    /**
     * 根据VIN码查询车型信息
     *
     * @param vinCode VIN码
     * @return 查询结果
     */
    VinQueryResult queryByVin(VinCode vinCode);

    /**
     * 检查适配器是否可用
     *
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 获取适配器优先级，值越小优先级越高
     *
     * @return 优先级
     */
    int getPriority();

}
