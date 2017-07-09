package com.bsc.playing.elastic;

import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by EdwinBrown on 7/9/2017.
 */
public class GeoTest {
    private static Logger log = LoggerFactory.getLogger(com.bsc.playing.elastic.GeoTest.class);
    public static final String channel = "wiki-rc-feed";
    public static String endpoint = "wss://open-data.api.satori.com";
    public static String appKey = "a6dB62fb8E5C23F13dA9Aba3a755fa80";


    public class InternalListener extends SubscriptionAdapter {
        private int counter = 0;
        private CountDownLatch success;

        /**
         * @param success
         */
        public InternalListener(CountDownLatch success) {
            this.success = success;
        }

        @Override
        public void onSubscriptionData(SubscriptionData data) {
            Iterable<AnyJson> iterator = data.getMessages();
            StringBuilder buf = new StringBuilder("{");
            Object val;

            for (AnyJson jayson : iterator) {
                String line = jayson.toString();

                log.info(jayson.toString());

                ++counter;
            } // for (AnyJson json : iterator) {

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

        @Test
    public void testGeo() {
        try {

            final RtmClient client = new RtmClientBuilder(endpoint, appKey)
                    .setListener(new RtmClientAdapter() {
                        @Override
                        public void onEnterConnected(RtmClient client) {
                            log.info("Connected to RTM!");
                        }

                        @Override
                        public void onLeaveStopped(RtmClient client) {
                            log.info("Client stopped");
                        }
                    })
                    .build();

            final CountDownLatch success = new CountDownLatch(100);

            InternalListener listener = new InternalListener(success);

            client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);

            client.start();

            success.await(5, TimeUnit.MINUTES);

            client.shutdown();
        } catch (Exception exp) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            exp.printStackTrace(pw);

            log.error(sw.toString());
        }

    }
}
