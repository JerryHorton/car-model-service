package cn.cug.sxy.domain.event.service;

import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

/**
 * @version 1.0
 * @Date 2025/8/15 13:35
 * @Description 死信处理动作执行器接口
 * @Author jerryhotton
 */

public interface IDeadLetterActionExecutor {

    /**
     * 转发到其他队列
     *
     * @param context     死信上下文
     * @param targetQueue 目标队列
     * @param channel     通道
     * @param deliveryTag 投递标签
     * @return 是否执行成功
     */
    boolean forwardToQueue(IDeadLetterHandler.DeadLetterContext context, String targetQueue, Channel channel, long deliveryTag) throws IOException;

    /**
     * 发送告警通知
     *
     * @param context    死信上下文
     * @param alertLevel 告警级别
     * @return 是否发送成功
     */
    boolean sendAlert(IDeadLetterHandler.DeadLetterContext context, AlertLevel alertLevel);

    /**
     * 存储到数据库
     *
     * @param context     死信上下文
     * @param storageType 存储类型
     * @return 是否存储成功
     */
    boolean storeToDatabase(IDeadLetterHandler.DeadLetterContext context, StorageType storageType);

    /**
     * 重新入队（谨慎使用）
     *
     * @param context     死信上下文
     * @param channel     通道
     * @param deliveryTag 投递标签
     * @param delayMillis 延迟时间
     * @return 是否重新入队成功
     */
    boolean requeue(IDeadLetterHandler.DeadLetterContext context, Channel channel, long deliveryTag, long delayMillis) throws IOException;

    /**
     * 自定义处理
     *
     * @param context      死信上下文
     * @param handlerName  处理器名称
     * @param customParams 自定义参数
     * @return 是否处理成功
     */
    boolean customHandle(IDeadLetterHandler.DeadLetterContext context, String handlerName, Object customParams);

    /**
     * 告警级别
     */
    @Getter
    @AllArgsConstructor
    enum AlertLevel {

        LOW("低级", "info"),
        MEDIUM("中级", "warning"),
        HIGH("高级", "error"),
        CRITICAL("紧急", "critical");

        private final String description;
        private final String level;

    }

    /**
     * 存储类型
     */
    @Getter
    @AllArgsConstructor
    enum StorageType {

        DEAD_LETTER_TABLE("死信表"),
        FAILED_MESSAGE_TABLE("失败消息表"),
        AUDIT_LOG_TABLE("审计日志表");

        private final String description;

        public String getDescription() {
            return description;
        }
    }

}
