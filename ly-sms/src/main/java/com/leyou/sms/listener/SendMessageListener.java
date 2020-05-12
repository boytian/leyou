package com.leyou.sms.listener;


import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.SMS_VERIFY_CODE_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;

/**
 * 监听消息 ，发送短信
 */
@Component
@RocketMQMessageListener(topic = SMS_TOPIC_NAME,
        selectorExpression = VERIFY_CODE_TAGS,
        consumerGroup = SMS_VERIFY_CODE_CONSUMER)
public class SendMessageListener implements RocketMQListener<String> {

    @Autowired
    private SmsHelper smsHelper;
    @Autowired
    private SmsProperties prop;

    @Override
    public void onMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        Map<String, String> msg = JsonUtils.toMap(message, String.class, String.class);
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }
        String sendMsg = JsonUtils.toString(msg);
        try {
            smsHelper.sendMessage(phone, prop.getSignName(), prop.getVerifyCodeTemplate(), sendMsg);
        } catch (LyException e) {

        }
    }
}