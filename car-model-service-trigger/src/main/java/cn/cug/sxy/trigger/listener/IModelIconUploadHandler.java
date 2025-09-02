package cn.cug.sxy.trigger.listener;

import cn.cug.sxy.domain.series.adapter.event.ModelIconUploadEvent;

/**
 * @version 1.0
 * @Date 2025/8/13 14:23
 * @Description 车型图标上传处理器接口
 * @Author jerryhotton
 */

public interface IModelIconUploadHandler {

    boolean handle(ModelIconUploadEvent.ModelIconData data);

}
