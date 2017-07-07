package com.bsc.playing.elastic;

import com.bsc.playing.elastic.data.WikiInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;

/**
 * Created by EdwinBrown on 6/18/2017.
 */
public class WikiSubscriptionListener extends SubscriptionAdapter {
    private static Logger log = LoggerFactory.getLogger(WikiSubscriptionListener.class);
    private int counter = 0;
    private CountDownLatch success;
    private ObjectMapper mapper;

    public WikiSubscriptionListener(CountDownLatch success) {
        this.success = success;
        mapper = new ObjectMapper();
    }


    @Override
    public void onSubscriptionData(SubscriptionData data) {
        Iterable<AnyJson> list = data.getMessages();

        try {
            for (AnyJson json : list) {

                WikiInfo wi = mapper.readValue(json.toString(), WikiInfo.class);

                log.info("Parsed: {} ", wi);

                if (wi.getGeoIp() != null) {
                    log.info("{}", json);
                }

            } // for (AnyJson json : data.getMessages()) {
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            e.printStackTrace(pw);

            log.error(sw.toString());

        }

        success.countDown();

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
