package com.bsc.playing.elastic;

import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by EdwinBrown on 6/18/2017.
 */
public class WikiElasticSubscriptionListener extends SubscriptionAdapter {
    private static Logger log = LoggerFactory.getLogger(WikiElasticSubscriptionListener.class);
    private int counter = 0;
    private CountDownLatch success;
    private TransportClient client;
    private BulkRequestBuilder bulkRequest;
    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public WikiElasticSubscriptionListener(TransportClient client, CountDownLatch success) {
        this.success = success;
        this.client = client;
        this.bulkRequest = client.prepareBulk();
    }


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

            bulkRequest.add(client.prepareIndex("wiki", "wiki", UUID.randomUUID().toString()).
                    setSource(buf.toString(), XContentType.JSON));

            log.info(buf.toString());

            ++counter;
        } // for (AnyJson json : data.getMessages()) {

        success.countDown();

        log.info("*** bulkRequest count: {}", bulkRequest.numberOfActions());

        if (bulkRequest.numberOfActions() >= 50) {
            BulkResponse bulkResponse = bulkRequest.get();
            if (!bulkResponse.hasFailures()) {
                log.info("**** bulk insert worked");

            } // if (!bulkResponse.hasFailures()) {
            else {
                log.error("ElasticSearch failure messages: {}", bulkResponse.buildFailureMessage());
                // process failures by iterating through each bulk response item
            }

            bulkRequest = client.prepareBulk();

        } // if (bulkRequest.numberOfActions() >= 50) {
    }

    @Override
    public void onSubscriptionError(SubscriptionError error) {
        log.error("Subscription error. Missed message count: {}", error.getMissedMessageCount());
        super.onSubscriptionError(error);
    }

    @Override
    public void onSubscriptionInfo(SubscriptionInfo info) {
        log.info("Subscription information: {}, reason: {} ", info.getInfo(), info.getReason());
        super.onSubscriptionInfo(info);
    }
}
