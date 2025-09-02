package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/12 14:33
 * @Description 任务(发送MQ)
 * @Author jerryhotton
 */

@Data
public class TaskPO {

    /**
     * 自增ID
     */
    private String id;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息主体
     */
    private String message;
    /**
     * CREATE-创建、PUBLISHED-已发布、PROCESSING-处理中、completed-完成、FAILED-失败、RETRY-重试中
     */
    private String state;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 最大重试次数
     */
    private Integer maxRetries;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 最后重试时间
     */
    private LocalDateTime lastRetryTime;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
