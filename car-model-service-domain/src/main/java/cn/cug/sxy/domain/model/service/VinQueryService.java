package cn.cug.sxy.domain.model.service;

import cn.cug.sxy.domain.model.adapter.port.IVinQueryPort;
import cn.cug.sxy.domain.model.model.valobj.VinCode;
import cn.cug.sxy.domain.model.model.valobj.VinQueryResult;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Date 2025/7/22 19:14
 * @Description VIN码查询服务实现
 * @Author jerryhotton
 */

@Service
public class VinQueryService implements IVinQueryService {

    private final IVinQueryPort vinQueryPort;

    public VinQueryService(IVinQueryPort vinQueryPort) {
        this.vinQueryPort = vinQueryPort;
    }

    @Override
    public VinQueryResult queryByVin(VinCode vinCode) {
        return vinQueryPort.queryByVin(vinCode);
    }
    
}
