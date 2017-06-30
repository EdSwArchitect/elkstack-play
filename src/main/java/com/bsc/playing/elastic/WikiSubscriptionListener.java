package com.bsc.playing.elastic;

import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by EdwinBrown on 6/18/2017.
 */
public class WikiSubscriptionListener extends SubscriptionAdapter {
    private static Logger log = LoggerFactory.getLogger(WikiSubscriptionListener.class);
    private int counter = 0;
    private CountDownLatch success;
    private TransportClient client;
    private BulkRequestBuilder bulkRequest;

    public WikiSubscriptionListener(TransportClient client, CountDownLatch success) {
        this.success = success;
        this.client = client;
        this.bulkRequest = client.prepareBulk();
    }


    @Override
    public void onSubscriptionData(SubscriptionData data) {
        List<Map> list = data.getMessagesAsType(Map.class);

        log.info("Number of messages: {}", list.size());

        for (Map map : list) {

//        for (AnyJson json : data.getMessages()) {
//            log.info("{}. Got message: {}", counter, json);

            log.info("{}. Got message: {}", counter, map);

//            Map foo = json.convertToType(Map.class);
//
//            log.info("{}, The map: {}", counter, foo);

            bulkRequest.add(client.prepareIndex("wiki", "wiki", UUID.randomUUID().toString()).
                    setSource(map));

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
