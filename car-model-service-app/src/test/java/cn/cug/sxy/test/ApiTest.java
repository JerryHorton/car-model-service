package cn.cug.sxy.test;

import cn.cug.sxy.api.ICarSeriesModelService;
import cn.cug.sxy.api.IStructureInstanceService;
import cn.cug.sxy.api.dto.InstanceQueryRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.CarModelVO;
import cn.cug.sxy.api.vo.InstanceBasePageVO;
import com.alibaba.fastjson2.JSON;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private MinioClient minioClient;

    @DubboReference(version = "1.0")
    private IStructureInstanceService structureInstanceService;

    @Test
    public void test_Minio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket("car-model-service")                // 存储桶名称
                        .object("exploded-views/group_1001/usage_1754635947460/20250808/922f81a58e024e3081fee6eb41335a5e.png")          // 对象（文件）路径
                        .expiry(24, TimeUnit.HOURS)        // 过期时间（24小时）
                        .build()
        );

        System.out.println("Presigned URL for download: " + url);
    }

    @Test
    public void test_Dubbo() {
        Response<InstanceBasePageVO> instanceBasePageVOResponse = structureInstanceService.queryInstances(new InstanceQueryRequestDTO());
        log.info("response: {}", JSON.toJSONString(instanceBasePageVOResponse.getData()));
    }
}
