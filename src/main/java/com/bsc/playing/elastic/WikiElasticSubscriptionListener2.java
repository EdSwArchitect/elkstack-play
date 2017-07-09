package com.bsc.playing.elastic;

import com.bsc.playing.elastic.data.GeoIp;
import com.bsc.playing.elastic.data.WikiInfo;
import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
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
    private String index;
    private String type;
    //"yyyy-MMM-dd HH:mm:ss"
    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);



    /**
     *
     * @param client
     * @param success
     * @param index
     * @param type
     */
    public WikiElasticSubscriptionListener2(TransportClient client, CountDownLatch success, String index, String type) {
        this.success = success;
        this.client = client;
        this.index = index;
        this.type = type;
        this.bulkRequest = client.prepareBulk();

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    @Override
    public void onSubscriptionData(SubscriptionData data) {

        Iterable<AnyJson> iterable = data.getMessages();

        WikiInfo wi = null;

        for (AnyJson jayson : iterable) {

            log.info("{}. Got message: {}", counter, jayson.toString());

            String json = jayson.toString();

            try {
                wi = WikiInfo.parseIt(json);

// {"action":"edit","change_size":1,"flags":null,"geo_ip":{"city":null,"country_name":"Norway","latitude":59.95,"longitude":10.75,"region_name":null},
// "hashtags":[],"is_anon":true,"is_bot":false,"is_minor":false,"is_new":false,"is_unpatrolled":false,"mentions":[],
// "ns":"Main","page_title":"Eljero Elia","parent_rev_id":"789383260","rev_id":"787513639","summary":null,
// "url":"https://en.wikipedia.org/w/index.php?diff=789383260&oldid=787513639","user":"51.174.232.183"}

                String action = wi.getAction();
                String flags = wi.getFlags();
                String[] hashTag = wi.getHashtags();
                String[] mentions = wi.getMentions();
                String ns  = wi.getNs();
                String pageTitle = wi.getPageTitle();
                String url = wi.getUrl();
                String user = wi.getUser();
                String parentRevId = wi.getParentRevId();
                String revId = wi.getRevId();
                String summary = wi.getSummary();
                long time = System.currentTimeMillis();

                XContentBuilder obj = jsonBuilder().startObject().field("action", action);

                obj = obj.field("timestamp", dateFormat.format(new Date(time)));


                obj = obj.field("change_size", wi.getChangeSize());

                if (flags != null) {
                    obj = obj.field("flags", flags);
                } // if (flags != null) {
                else {
                    obj = obj.nullField("flags");
                }

                GeoIp geoIp = wi.getGeoIp();

                if (geoIp != null) {
                    String city = geoIp.getCity();
                    String countryName = geoIp.getCountryName();
                    String regionName = geoIp.getRegionName();

                    obj = obj.startObject("geo_ip");

                    if (city != null) {
                        obj = obj.field("city", city);
                    }
                    else {
                        obj = obj.nullField("city");
                    }
                    if (countryName != null) {
                        obj = obj.field("country_name", countryName);
                    }
                    else {
                        obj = obj.nullField("country_name");
                    }

                    obj = obj.field("latitude", geoIp.getLatitude());
                    obj = obj.field("longitude", geoIp.getLongitude());

                    if (regionName != null) {
                        obj = obj.field("region_name", regionName);
                    }
                    else {
                        obj = obj.nullField("region_name");
                    }

                    obj = obj.endObject();

                } // if (geoIp != null) {
                else {
                    obj = obj.nullField("geo_ip");
                }

                if (hashTag != null) {
                    obj = obj.array("hashtags", hashTag);
                } // if (hashTag != null) {
                else {
                    obj = obj.startArray("hashtags").nullValue().endArray();
                }

                obj = obj.field("is_anon", wi.isAnon());
                obj = obj.field("is_bot", wi.isBot());
                obj = obj.field("is_minor", wi.isMinor());
                obj = obj.field("is_new", wi.isNew());
                obj = obj.field("is_unpatrolled", wi.isUnparolled());

                if (mentions != null) {
                    obj = obj.array("mentions", mentions);
                } // if (hashTag != null) {
                else {
                    obj = obj.startArray("mentions").nullValue().endArray();
                }

                if (ns != null) {
                    obj = obj.field("ns", wi.getNs());
                }
                else {
                    obj = obj.nullField("ns");
                }

                if (pageTitle != null) {
                    obj = obj.field("page_title", pageTitle);
                }
                else {
                    obj = obj.nullField("page_title");
                }

                if (parentRevId != null) {
                    obj = obj.field("parent_rev_d", parentRevId);
                }
                else {
                    obj = obj.nullField("parent_rev_id");
                }

                if (revId != null) {
                    obj = obj.field("rev_id", revId);
                }
                else {
                    obj = obj.nullField("rev_id");
                }

                if (summary != null) {
                    obj = obj.field("summary", summary);
                }
                else {
                    obj = obj.nullField("summary");
                }


                if (url != null) {
                    obj = obj.field("url", url);
                }
                else {
                    obj = obj.nullField("url");
                }

                if (user != null) {
                    obj = obj.field("user", user);
                }
                else {
                    obj = obj.nullField("user");
                }

                obj = obj.endObject();

// either use client#prepare, or use Requests# to directly build index/delete requests
                bulkRequest.add(client.prepareIndex(index, type, UUID.randomUUID().toString())
                        .setSource(obj));

            } catch (IOException e) {

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                e.printStackTrace(pw);

                log.error(sw.toString());
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
