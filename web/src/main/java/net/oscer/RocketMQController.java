package net.oscer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
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
        try{
            mqProducerService.send("123");
        }catch (Exception e){
            if(e instanceof RemotingConnectException){
                System.out.println("rocktemq connect error");
            }
            e.printStackTrace();
        }

    }

    @GetMapping("/sendResult")
    public void sendResult(){
        mqProducerService.sendMsg("sendResult");
    }

    @GetMapping("/sendDelayMsg")
    public void sendDelayMsg(){
        mqProducerService.sendDelayMsg("10",10);
        mqProducerService.sendDelayMsg("1",1);
    }

    @GetMapping("/sendTag")
    public void sendTag() {
        SendResult sendResult = mqProducerService.sendTagMsg("带有tag的字符消息");
    }

}
