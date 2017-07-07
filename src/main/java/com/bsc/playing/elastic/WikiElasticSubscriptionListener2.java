package com.bsc.playing.elastic;

import com.bsc.playing.elastic.data.WikiInfo;
import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Reads the wiki stream and inserts into ElasticSearch. The ES format is 100% correct. But the manner in which
 * it's done is inefficient. I know it. Leave me alone.
 *
 * Created by EdwinBrown on 6/18/2017.
 */
public class WikiElasticSubscriptionListener2 extends SubscriptionAdapter {
    private static Logger log = LoggerFactory.getLogger(WikiElasticSubscriptionListener2.class);
    private int counter = 0;
    private CountDownLatch success;
    private TransportClient client;
    private BulkRequestBuilder bulkRequest;

    public WikiElasticSubscriptionListener2(TransportClient client, CountDownLatch success) {
        this.success = success;
        this.client = client;
        this.bulkRequest = client.prepareBulk();
    }


    @Override
    public void onSubscriptionData(SubscriptionData data) {

        List<String> list = data.getMessagesAsStrings();

        log.info("Number of messages: {}", list.size());
        WikiInfo wi = null;

        for (String json : list) {

            log.info("{}. Got message: {}", counter, json);

            try {
                wi = WikiInfo.parseIt(json);

// {"action":"edit","change_size":1,"flags":null,"geo_ip":{"city":null,"country_name":"Norway","latitude":59.95,"longitude":10.75,"region_name":null},
// "hashtags":[],"is_anon":true,"is_bot":false,"is_minor":false,"is_new":false,"is_unpatrolled":false,"mentions":[],
// "ns":"Main","page_title":"Eljero Elia","parent_rev_id":"789383260","rev_id":"787513639","summary":null,
// "url":"https://en.wikipedia.org/w/index.php?diff=789383260&oldid=787513639","user":"51.174.232.183"}

                String flags = wi.getFlags();

                //TODO Doesn't account for null values. Do this the long hard way :(


// either use client#prepare, or use Requests# to directly build index/delete requests
                bulkRequest.add(client.prepareIndex("twitter", "tweet", "1")
                        .setSource(jsonBuilder()
                                .startObject()
                                .field("action", wi.getAction())
                                .field("change_size", wi.getChangeSize())
                                .field("flags", wi.getFlags())
                                // geo_ip
                                .field("action", wi.getAction())
                                .field("hashtags", wi.getHashtags())
                                .startArray().array("hashtags", wi.getHashtags()).endArray()
                                .field("is_anon", wi.isAnon())
                                .field("is_bot", wi.isBot())
                                .field("is_minor", wi.isMinor())
                                .field("is_new", wi.isNew())
                                .field("is_unpatrolled", wi.isUnparolled())
                                .startArray().array("mentions", wi.getMentions()).endArray()
                                .field("ns", wi.getNs())
                                .field("page_title", wi.getPageTitle())
                                .field("parent_rev_id", wi.getParentRevId())
                                .field("rev_id", wi.getRevId())
                                .field("summary", wi.getSummary())
                                .field("url", wi.getUrl())
                                .field("user", wi.getUser())
                                .endObject()
                        )
                );

            } catch (IOException e) {
                e.printStackTrace();
            }

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
