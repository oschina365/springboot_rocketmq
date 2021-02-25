package net.oscer;

import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {

    @Autowired
    private MQProducerService mqProducerService;

    @GetMapping("/send")
    public void send() {
        mqProducerService.send("123");
    }

    @GetMapping("/sendTag")
    public void sendTag() {
        SendResult sendResult = mqProducerService.sendTagMsg("带有tag的字符消息");
    }

}
