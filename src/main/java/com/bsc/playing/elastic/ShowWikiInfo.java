package com.bsc.playing.elastic;

import com.satori.rtm.RtmClient;
import com.satori.rtm.RtmClientAdapter;
import com.satori.rtm.RtmClientBuilder;
import com.satori.rtm.SubscriptionMode;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * https://www.satori.com/?utm_expid=.08UMh4RnQ8SHNc3yk2i52g.0&utm_referrer=
 * <p>
 * Created by EdwinBrown on 6/17/2017.
 */
public class ShowWikiInfo {
    public static final String channel = "wiki-rc-feed";
    public static Logger log = LoggerFactory.getLogger(ShowWikiInfo.class);
    public static String endpoint = "wss://open-data.api.satori.com";
    public static String appKey = "a6dB62fb8E5C23F13dA9Aba3a755fa80";

    /**
     * @param args
     */
    public static void main(String... args) {
        log.info("Hi, Ed");

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

            WikiSubscriptionListener listener = new WikiSubscriptionListener(success);

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
