package cn.cug.sxy.infrastructure.adapter.port;

import cn.cug.sxy.domain.series.adapter.port.IVinQueryPort;
import cn.cug.sxy.domain.series.model.valobj.VinCode;
import cn.cug.sxy.domain.series.model.valobj.VinQueryResult;
import cn.cug.sxy.infrastructure.adapter.port.config.VinQueryPortConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 * @Date 2025/7/22 17:25
 * @Description VIN码查询适配端口抽象基类
 * @Author jerryhotton
 */

public abstract class AbstractVinQueryPort implements IVinQueryPort {

    protected final VinQueryPortConfig config;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 最大重试次数
     */
    protected static final int MAX_RETRY_COUNT = 3;
    /**
     * 重试间隔（毫秒）
     */
    protected static final long RETRY_INTERVAL = 1000;

    public AbstractVinQueryPort(VinQueryPortConfig config) {
        this.config = config;
    }

    /**
     * 查询VIN码信息
     *
     * @param vinCode VIN码
     * @return 查询结果
     */
    @Override
    public VinQueryResult queryByVin(VinCode vinCode) {
        if (vinCode == null) {
            throw new IllegalArgumentException("VIN码不能为空");
        }
        if (!isAvailable()) {
            logger.warn("适配端口 {} 不可用", getName());
            return VinQueryResult.failure("VIN码查询服务不可用");
        }
        // 带重试的查询
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            try {
                VinQueryResult result = doQueryByVin(vinCode);
                if (result.isSuccess()) {
                    return result;
                }
                // 如果查询失败但不是因为服务不可用，直接返回失败结果
                if (i == MAX_RETRY_COUNT - 1) {
                    return result;
                }
                logger.warn("适配端口 {} 查询VIN码 {} 失败: {}，准备重试第 {} 次",
                        getName(), vinCode.getCode(), result.getErrorMessage(), i + 1);
                // 重试前等待
                Thread.sleep(RETRY_INTERVAL);
            } catch (Exception e) {
                logger.error("适配端口 {} 查询VIN码 {} 异常", getName(), vinCode.getCode(), e);
                if (i == MAX_RETRY_COUNT - 1) {
                    return VinQueryResult.failure("VIN码查询服务异常: " + e.getMessage());
                }
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return VinQueryResult.failure("VIN码查询被中断");
                }
            }
        }

        return VinQueryResult.failure("VIN码查询服务重试多次仍然失败");
    }

    /**
     * 实际执行VIN码查询的方法，由子类实现
     *
     * @param vinCode VIN码
     * @return 查询结果
     */
    protected abstract VinQueryResult doQueryByVin(VinCode vinCode);

}
