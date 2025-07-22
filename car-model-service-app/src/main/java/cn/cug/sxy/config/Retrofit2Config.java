package cn.cug.sxy.config;

import cn.cug.sxy.infrastructure.gateway.ICarServiceGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class Retrofit2Config {

    @Value("${vin.query.ports.default.base-url}")
    private String defaultBaseUrl;

    @Bean
    public ICarServiceGateway carServiceGateway() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(defaultBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(ICarServiceGateway.class);
    }

}
