package cn.cug.sxy.trigger.listener;

import cn.cug.sxy.domain.series.adapter.event.ModelIconUploadEvent;
import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.service.ICarModelCommandService;
import cn.cug.sxy.domain.series.service.ICarModelQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Date 2025/8/13 14:29
 * @Description 车型图标创建上传处理器
 * @Author jerryhotton
 */

@Slf4j
@Component(value = "CREATE")
public class ModelIconCreateUploadHandler implements IModelIconUploadHandler {

    private final ICarModelCommandService carModelCommandService;
    private final ICarModelQueryService carModelQueryService;

    public ModelIconCreateUploadHandler(
            ICarModelCommandService carModelCommandService,
            ICarModelQueryService carModelQueryService) {
        this.carModelCommandService = carModelCommandService;
        this.carModelQueryService = carModelQueryService;
    }

    @Override
    public boolean handle(ModelIconUploadEvent.ModelIconData data) {
        try {
            // 1. 上传文件到存储服务
            String iconPath = carModelCommandService.uploadModelIcon(data.getFileData(), data.getFileName(), data.getContentType(), data.getModelId());
            if (iconPath == null) {
                return false;
            }
            // 2. 更新数据库中的图标路径
            CarModelEntity modelEntity = carModelQueryService.getById(new ModelId(data.getModelId()));
            if (modelEntity != null) {
                modelEntity.setIconPath(iconPath);
                int updateCount = carModelCommandService.updateModelIconPath(modelEntity);
                if (updateCount > 0) {
                    log.info("车型图标路径更新成功，modelId: {}, iconPath: {}", data.getModelId(), iconPath);
                    return true;
                } else {
                    log.warn("车型图标路径更新失败，modelId: {}", data.getModelId());
                    // 删除已上传的文件
                    carModelCommandService.deleteModelIcon(iconPath);
                    return false;
                }
            } else {
                log.warn("车型不存在，modelId: {}", data.getModelId());
                // 删除已上传的文件
                carModelCommandService.deleteModelIcon(iconPath);
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
