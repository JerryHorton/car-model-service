package cn.cug.sxy.infrastructure.adapter.port;

import cn.cug.sxy.domain.series.model.valobj.ModelCode;
import cn.cug.sxy.domain.series.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.VinCode;
import cn.cug.sxy.domain.series.model.valobj.VinQueryResult;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.infrastructure.adapter.port.config.VinQueryPortConfig;
import cn.cug.sxy.infrastructure.gateway.ICarServiceGateway;
import cn.cug.sxy.infrastructure.gateway.dto.VinQueryRequestDTO;
import cn.cug.sxy.infrastructure.gateway.dto.VinQueryResponseDTO;
import cn.cug.sxy.infrastructure.gateway.response.Response;
import cn.cug.sxy.types.enums.ResponseCode;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/22 17:49
 * @Description 默认VIN码查询适配端口实现
 * @Author jerryhotton
 */

@Component
public class DefaultVinQueryPort extends AbstractVinQueryPort {

    private static final String PORT_NAME = "default";

    private final ICarServiceGateway carServiceGateway;

    public DefaultVinQueryPort(VinQueryPortConfig config, ICarServiceGateway carServiceGateway) {
        super(config);
        this.carServiceGateway = carServiceGateway;
    }

    @Override
    protected VinQueryResult doQueryByVin(VinCode vinCode) {
        VinQueryPortConfig.PortConfig portConfig = config.getPorts().get((PORT_NAME));
        if (portConfig == null) {
            logger.error("适配器 {} 配置不存在", PORT_NAME);
            return VinQueryResult.failure("适配器配置不存在");
        }
        try {
            // 调用第三方API查询Vin
            VinQueryRequestDTO requestDTO = VinQueryRequestDTO.builder()
                    .vin(vinCode.getCode())
                    .build();
            Call<Response<VinQueryResponseDTO>> queryCarModelByVinCall = carServiceGateway.queryCarModelByVin(requestDTO);
            Response<VinQueryResponseDTO> response = queryCarModelByVinCall.execute().body();
            if (response == null || !ResponseCode.SUCCESS.getCode().equals(response.getCode())) {
                return VinQueryResult.failure(null == response ? ResponseCode.NULL_RESPONSE.getInfo() : response.getInfo());
            }
            VinQueryResponseDTO responseDTO = response.getData();

            return VinQueryResult.success(
                    new ModelCode(responseDTO.getModelCode()),
                    new Brand(responseDTO.getBrand()),
                    responseDTO.getModelName(),
                    PowerType.fromCode(responseDTO.getPowerType())
            );
        } catch (Exception e) {
            logger.error("适配端口 {} 查询VIN码 {} 异常", PORT_NAME, vinCode.getCode(), e);
            return VinQueryResult.failure("查询异常: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return PORT_NAME;
    }

    @Override
    public boolean isAvailable() {
        VinQueryPortConfig.PortConfig portConfig = config.getPorts().get(PORT_NAME);
        return config.isEnabled() && portConfig != null && portConfig.isEnabled();
    }

    @Override
    public int getPriority() {
        return Optional.ofNullable(config.getPorts().get(PORT_NAME))
                .map(VinQueryPortConfig.PortConfig::getPriority)
                .orElse(100);
    }

}
