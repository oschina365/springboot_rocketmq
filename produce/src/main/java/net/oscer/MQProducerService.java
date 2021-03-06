package net.oscer;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @create 2021-02-25 13:37
 **/
@Component
public class MQProducerService {

    @Value("${rocketmq.producer.send-message-timeout}")
    private Integer messageTimeOut;

    // 建议正常规模项目统一用一个TOPIC
    private static final String topic = "Pro_Group";

    // 直接注入使用，用于发送消息到broker服务器
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 普通发送（这里的参数对象User可以随意定义，可以发送个对象，也可以是字符串等）
     */
    public void send(String message) {
        rocketMQTemplate.convertAndSend(topic + ":tag1", message);
//        rocketMQTemplate.send(topic + ":tag1", MessageBuilder.withPayload(message).build()); // 等价于上面一行
    }

    /**
     * 发送同步消息（sendResult为返回的发送结果）
     *      // 消息发送成功
     *     SEND_OK,
     *
     *     // 消息发送成功，但是服务器刷盘超时，消息已经进入服务器队列，只有此时服务器宕机，消息才会丢失
     *     FLUSH_DISK_TIMEOUT,
     *
     *     // 消息发送成功，但是服务器同步到 Slave 时超时，消息已经进入服务器队列，只有此时服务器宕机，消息才会丢失
     *     FLUSH_SLAVE_TIMEOUT,
     *
     *     // 消息发送成功，但是此时 slave 不可用，消息已经进入服务器队列，只有此时服务器宕机，消息才会丢
     *     SLAVE_NOT_AVAILABLE
     */

    public SendResult sendMsg(String msgBody) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build());
        System.out.println(String.format("sendMsg={%s}", JSON.toJSONString(sendResult)));
        return sendResult;
    }

    /**
     * 发送异步消息（适合对响应时间敏感的业务场景）
     */
    public void sendAsyncMsg(String msgBody) {
        rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(msgBody).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                // 处理消息发送成功逻辑
            }

            @Override
            public void onException(Throwable throwable) {
                // 处理消息发送异常逻辑
            }
        });
    }

    /**
     * 发送延时消息<br/>
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public void sendDelayMsg(String msgBody, int delayLevel) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build(), messageTimeOut, delayLevel);
        System.out.println(String.format("sendMsg={%s}", JSON.toJSONString(sendResult)));
    }

    /**
     * 发送单向消息（不关心发送结果，如日志）
     */
    public void sendOneWayMsg(String msgBody) {
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(msgBody).build());
    }

    /**
     * 发送带tag的消息，直接在topic后面加上":tag"
     */
    public SendResult sendTagMsg(String msgBody) {
        return rocketMQTemplate.syncSend(topic + ":tag2", MessageBuilder.withPayload(msgBody).build());
    }

}
