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
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

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

        Iterable<AnyJson> list = data.getMessages();
        StringBuilder buf = new StringBuilder();
        long time;
        StringBuilder newField = new StringBuilder();

        for (AnyJson jayson : list) {
            time = System.currentTimeMillis();

            newField.setLength(0);
            newField.append("\"timestamp\" : \"").append(dateFormat.format(new Date(time)));
            newField.append("\", ");

            buf.setLength(0);
            buf.append(jayson.toString());

            buf.insert(1, newField);

            log.info(buf.toString());

            producer.send(new ProducerRecord<String, String>("wiki-topic", UUID.randomUUID().toString(), buf.toString
                    ()));


            ++counter;
        } // for (AnyJson json : data.getMessages()) {
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
