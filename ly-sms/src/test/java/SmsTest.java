
import com.leyou.LySmsApplication;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsHelper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySmsApplication.class)
public class SmsTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private SmsHelper smsHelper;
    @Autowired
    private SmsProperties prop;

    @Test
    public void send(){
        Map<String,String> map = new HashMap<>();
        map.put("phone", "17639319984");
        map.put("code", "666777");
        smsHelper.sendMessage(map.get("phone"),prop.getSignName(),prop.getVerifyCodeTemplate(), JsonUtils.toString(map));
    }

    @Test
    public void testSendMessage() throws InterruptedException {
        Map<String,String> map = new HashMap<>();
        map.put("phone", "17639319984");
        map.put("code", "343434");
        rocketMQTemplate.convertAndSend(SMS_TOPIC_NAME+":"+VERIFY_CODE_TAGS,map);
    }
}