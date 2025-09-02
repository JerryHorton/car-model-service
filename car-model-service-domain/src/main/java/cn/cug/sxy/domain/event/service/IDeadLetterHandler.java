package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/8/15 11:36
 * @Description 死信处理器接口
 * @Author jerryhotton
 */

public interface IDeadLetterHandler {

    /**
     * 死信处理结果
     */
    enum DeadLetterAction {
        /**
         * 记录并忽略
         */
        LOG_AND_IGNORE,
        /**
         * 重新入队（谨慎使用）
         */
        REQUEUE,
        /**
         * 转发到其他队列
         */
        FORWARD_TO_QUEUE,
        /**
         * 发送告警通知
         */
        SEND_ALERT,
        /**
         * 存储到数据库
         */
        STORE_TO_DATABASE,
        /**
         * 自定义处理
         */
        CUSTOM_HANDLE
    }

    /**
     * 死信处理上下文
     */
    @Getter
    @AllArgsConstructor
    class DeadLetterContext {

        private final String messageId;
        private final String originalQueue;
        private final String originalExchange;
        private final String originalRoutingKey;
        private final String failureReason;
        private final int retryCount;
        private final long firstFailureTime;
        private final BaseEvent.EventMessage<?> eventMessage;

        /**
         * 获取消息主题
         */
        public String getMessageTopic() {
            return eventMessage != null ? eventMessage.getTopic() : "unknown";
        }

        /**
         * 获取失败持续时间
         */
        public long getFailureDuration() {
            return System.currentTimeMillis() - firstFailureTime;
        }

    }

    /**
     * 处理死信消息
     *
     * @param context 死信上下文
     * @return 处理动作
     */
    DeadLetterAction handleDeadLetter(DeadLetterContext context);

    /**
     * 是否支持处理指定类型的消息
     *
     * @param messageTopic 消息主题
     * @return 是否支持
     */
    boolean supports(String messageTopic);

    /**
     * 获取处理器优先级（数字越小优先级越高）
     *
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }

    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    String getHandlerName();

}
