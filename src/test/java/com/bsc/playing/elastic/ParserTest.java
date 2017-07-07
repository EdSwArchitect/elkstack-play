package com.bsc.playing.elastic;

import com.bsc.playing.elastic.data.WikiInfo;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by EdwinBrown on 7/4/2017.
 */
public class ParserTest {

    String json = "{\"action\":\"hit\",\"change_size\":12,\"flags\":\"MD\"," +
            "\"geo_ip\":{\"city\":\"Jinju\"," +
            "\"country_name\":\"Republic of Korea\",\"latitude\":35.1928,\"longitude\":128.0847," +
            "\"region_name\":\"Gyeongsangnam-do\"},\"hashtags\":[\"strang\",\"strang2\"],\"is_anon\":true," +
            "\"is_bot\":false," +
            "\"is_minor\":false,\"is_new\":false,\"is_unpatrolled\":false,\"mentions\":[],\"ns\":\"Special\",\"page_title\":\"Special:Log/abusefilter\",\"summary\":\"1.254.6.66 triggered [[Special:AbuseFilter/345|filter 345]], performing the action \\\"edit\\\" on [[MIL-STD-188]]. Actions taken: Warn ([[Special:AbuseLog/18840432|details]])\",\"user\":\"1.254.6.66\"}";

    @Test
    public void testParser() {
        ObjectMapper mapper = new ObjectMapper();
        JsonToken token;
        String fieldName;

        try {
            WikiInfo wi = mapper.readValue(json, WikiInfo.class);
            JsonFactory f = new JsonFactory();
            JsonParser p = f.createParser(json);

            token = p.nextToken();

            while (token != null && token != JsonToken.NOT_AVAILABLE) {
                fieldName = token.name();
                System.out.println("***fieldName: '" + fieldName + "'");

                if (fieldName.equals("FIELD_NAME")) {
                    token = p.nextToken();

                    if (!token.name().equals("START_OBJECT")) {
                        if (token.isBoolean()) {
                            System.out.format("\tName '%s' - %b%n", p.getCurrentName(), p.getBooleanValue());
                        }
                        else if (token.isNumeric()) {
                            if (p.getCurrentName().equals("VALUE_NUMBER_FLOAT")) {
                                System.out.format("\tName '%s' - %f%n", p.getCurrentName(), p.getFloatValue());

                            }
                            else if (p.getCurrentName().equals("VALUE_NUMBER_INT")) {
                                System.out.format("\tName '%s' - %d%n", p.getCurrentName(), p.getIntValue());
                            }
                        }
                        else if (token.isStructStart()) {
                            System.out.format("\tStruct start: Name '%s' - '%s'%n", p.getCurrentName(), p.getText());
                        }
                        else if(token.isStructEnd()) {
                            System.out.format("\tStruct end: Name '%s' - %s%n", p.getCurrentName(), p.getText());
                        }
                        else if (token.isScalarValue()) {
                            System.out.format("\tScalar value: Name '%s' - %s%n", p.getCurrentName(), p.getText());
                        }
                    }
                    else {
                        System.out.format("\tStarting object '%s', tokenName: '%s'%n", p.getCurrentName(), token.name
                                ());
                    }


//                    System.out.format("\tname: '%s' value: '%s' boolean -'%b' numeric - '%b' scalar - %b --- last " +
//                                    "name %s%n", p
//                                    .getCurrentName(), p.getText(),
//                            token.isBoolean(),
//                            token.isNumeric(), token.isScalarValue(), token.name());
                } // if (fieldName.equals("FIELD_NAME")) {
                else if (fieldName.equals("VALUE_STRING")) {
                    System.out.format("\tValue_String: Name '%s' - %s%n", p.getCurrentName(), p.getText());
                } // else if (fieldName.equals("VALUE_STRING")) {
                else {
                    System.out.format("--> fieldName: '%s'%n", fieldName);
                }
                token = p.nextToken();
            }

//            while (p.nextToken() == JsonToken.FIELD_NAME) {
//                String fieldName = p.getCurrentName();
//                System.out.println("FieldName: '" + fieldName + "'");
//
////                if (fieldName.equals("action")) {
////                    System.out.println("\t->action virst getValue: '" + p.getValueAsString() + "'");
////                    token = p.nextToken();
////                    System.out.println("\t\ttoken name: '" + token.name() + "' - " + p.getText());
////                    token = p.nextToken();
////                    System.out.println("\t\ttoken name: '" + token.name() + "'");
////
////                }
////
//                switch(fieldName) {
//                    case "action":
//                    case "change_size":
//                    case "flags":
//                    case "city":
//                    case "country_name":
//                    case "latitude":
//                    case "longitude":
//                    case "region_name":
//                    case "hashtags":
//                    case "is_anon":
//                    case "is_bot":
//                    case "is_minor":
//                    case "is_new":
//                    case "is_unpatrolled":
//                    case "mentions":
//                    case "ns":
//                    case "page_title":
//                    case "summary":
//                    case "user":
//                        token = p.nextToken();
//                        System.out.println("\t\ttoken name: '" + token.name() + "' - " + p.getText());
//                        break;
//                    case "geo_ip":
//                        token = p.nextToken();
//                        System.out.println("\t\ttoken name: '" + token.name() + "' - " + p.getText());
//                        break;
//                    default:
//                        break;
//                }
//
////                while ((token = p.nextToken()) != JsonToken.END_OBJECT) {
////                    String nameField = p.getCurrentName();
////
////                    System.out.println("\tnameField: " + nameField); // + " - nextValue: '" + p.nextValue().asString
////
////                    // () + "'");
////                } // while (p.nextToken() != JsonToken.END_OBJECT) {
//            } // while (p.nextToken() == JsonToken.FIELD_NAME) {

            System.out.println(wi);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
