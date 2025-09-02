package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/15 11:38
 * @Description 死信管理器接口
 * @Author jerryhotton
 */

public interface IDeadLetterManager {

    /**
     * 处理死信消息
     *
     * @param event   事件对象
     * @param message 原始消息
     * @param channel 通道
     * @throws IOException IO异常
     */
    void handleDeadLetter(BaseEvent.EventMessage<?> event, Message message, Channel channel) throws IOException;

    /**
     * 获取所有处理器
     *
     * @return 处理器列表
     */
    List<IDeadLetterHandler> getAllHandlers();

    /**
     * 根据消息主题获取合适的处理器
     *
     * @param messageTopic 消息主题
     * @return 处理器列表（按优先级排序）
     */
    List<IDeadLetterHandler> getHandlersForTopic(String messageTopic);

    /**
     * 获取死信统计信息（可选功能）
     *
     * @return 统计信息
     */
    default DeadLetterStats getDeadLetterStats() {
        // 默认实现：返回空统计
        return new DeadLetterStats(0, 0, 0, 0, "暂无统计");
    }

    /**
     * 死信统计信息
     */
    @Getter
    @AllArgsConstructor
    class DeadLetterStats {

        private final long totalDeadLetters;
        private final long todayDeadLetters;
        private final long processedDeadLetters;
        private final long failedDeadLetters;
        private final String mostFailedTopic;

        @Override
        public String toString() {
            return String.format("DeadLetterStats[total=%d, today=%d, processed=%d, failed=%d, mostFailedTopic=%s]",
                    totalDeadLetters, todayDeadLetters, processedDeadLetters, failedDeadLetters, mostFailedTopic);
        }

    }

}
