package com.bsc.playing.elastic;

import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import com.satori.rtm.model.SubscriptionError;
import com.satori.rtm.model.SubscriptionInfo;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by EdwinBrown on 6/18/2017.
 */
public class WikiFileSubscriptionListener extends SubscriptionAdapter {
    private static Logger log = LoggerFactory.getLogger(WikiFileSubscriptionListener.class);
    private int counter = 0;
    private CountDownLatch success;
    private String directory;
    private String fileName;
    private PrintWriter out;
    private boolean firstOne = true;

    /**
     *
     * @param directory
     * @param fileName
     * @param success
     */
    public WikiFileSubscriptionListener(String directory, String fileName, CountDownLatch success) {
        this.success = success;
        this.directory = directory;
        this.fileName = fileName;

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(directory + "/" + fileName + "-" + System.currentTimeMillis() +
                    ".json");
            out = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
//            out.println("{");
        } catch (FileNotFoundException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            log.error(sw.toString());
        } catch (UnsupportedEncodingException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            log.error(sw.toString());
        }

        if (out == null) {
            log.error("File not created.");
            System.exit(1);
        }

    }

    @Override
    public void onSubscriptionData(SubscriptionData data) {
        Iterable<AnyJson> iterator = data.getMessages();
        StringBuilder buf = new StringBuilder("{");
        Object val;

        for (AnyJson json : iterator) {
            Map<String, Object> map = (Map<String, Object>)json.convertToType(Map.class);
//            if (!firstOne) {
//                out.println("," + str.toString());
//            }
//            else {
//                out.println(map.toString());

                for (String key : map.keySet()) {
                    if (buf.length() > 1) {
                        buf.append(", ");
                    }

                    val = map.get(key);

                    if (val instanceof String) {
                        buf.append('"').append(key).append("\" : \"").append(val).append("\"");
                    }
                    else {
                        buf.append('"').append(key).append("\" : ").append(map.get(key));
                    }
                }

                buf.append("}");
                out.println(buf.toString());
                buf.setLength(1);

//                firstOne = false;
//            }
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

    /**
     *
     */
    public void cleanup() {
//        out.println("}");
        out.flush();
        out.close();
        firstOne = true;
    }
}
