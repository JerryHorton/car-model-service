package cn.cug.sxy.infrastructure.gateway;

import cn.cug.sxy.infrastructure.gateway.dto.VinQueryRequestDTO;
import cn.cug.sxy.infrastructure.gateway.dto.VinQueryResponseDTO;
import cn.cug.sxy.infrastructure.gateway.response.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @version 1.0
 * @Date 2025/7/22 18:37
 * @Description 汽车厂商API
 * @Author jerryhotton
 */

public interface ICarServiceGateway {

    @POST("api/v1/vin/query_car_model_by_vin")
    Call<Response<VinQueryResponseDTO>> queryCarModelByVin(@Body VinQueryRequestDTO vinQueryRequestDTO);

}
