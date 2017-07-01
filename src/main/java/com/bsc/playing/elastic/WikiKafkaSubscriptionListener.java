package com.bsc.playing.elastic;

import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.elasticsearch.common.util.concurrent.CountDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by EdwinBrown on 6/18/2017.
 */
public class WikiKafkaSubscriptionListener extends SubscriptionAdapter {
    private static Logger log = LoggerFactory.getLogger(WikiKafkaSubscriptionListener.class);
    private int counter = 0;
    private CountDownLatch success;
    private Producer<String, String>producer;

    /**
     *
     * @param success
     */
    public WikiKafkaSubscriptionListener(CountDownLatch success) {
        this.success = success;
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }

    /**
     *
     * @param data
     */
    @Override
    public void onSubscriptionData(SubscriptionData data) {
        Iterable<AnyJson> iterator = data.getMessages();
        StringBuilder buf = new StringBuilder("{");
        Object val;

        for (AnyJson json : iterator) {
            Map<String, Object> map = (Map<String, Object>) json.convertToType(Map.class);

            for (String key : map.keySet()) {
                if (buf.length() > 1) {
                    buf.append(", ");
                }

                val = map.get(key);

                if (val instanceof String) {
                    buf.append('"').append(key).append("\" : \"").append(val).append("\"");
                }
                else if (val instanceof Number) {
                    buf.append('"').append(key).append("\" : ").append(val).append("");
                }
                else {
                    buf.append('"').append(key).append("\" : ").append(val);
                }
            } // for (String key : map.keySet()) {

            buf.append("}");

            producer.send(new ProducerRecord<String, String>("wiki-topic", UUID.randomUUID().toString(), buf.toString
                    ()));

            buf.setLength(1);
            ++counter;
        } // for (AnyJson json : iterator) {

        success.countDown();

    }

    /**
     *
     * @param error
     */
    @Override
    public void onSubscriptionError(SubscriptionError error) {
        log.error("Subscription error. Missed message count: {}", error.getMissedMessageCount());
        super.onSubscriptionError(error);
    }

    /**
     *
     * @param info
     */
    @Override
    public void onSubscriptionInfo(SubscriptionInfo info) {
        log.info("Subscription information: {}, reason: {} ", info.getInfo(), info.getReason());
        super.onSubscriptionInfo(info);
    }
}
