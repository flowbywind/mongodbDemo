package com.bj58.bootweb.Controller;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.javafx.scene.layout.region.BackgroundImage;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;
import static com.mongodb.client.model.Filters.*;
import static java.util.Arrays.asList;

/**
 * Created by admin on 2016/8/12.
 */
@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/mongo", method = RequestMethod.GET)
public class MongoController {
    private MongoClient client = new MongoClient();
    private MongoDatabase db = client.getDatabase("test");

    @RequestMapping(value = "/insert", method = RequestMethod.GET)
    @ResponseBody
    public String insert() {
        MongoCollection collection = db.getCollection("restaurants");
        org.bson.Document document = new Document("address",
                new Document()
                        .append("street", "2 Avenue")
                        .append("zipcode", "10075")
                        .append("coord", asList(-73.9557413, 40.7720266)))
                .append("borough", "Manhattan")
                .append("cuisine", "Italian")
                .append("grades", asList(
                        new Document().
                                append("date", new Date().toString())
                                .append("grade", "A")
                                .append("score", 11),
                        new Document()
                                .append("date", new Date().toString())
                                .append("grade", "B")
                                .append("score", 17)
                ))
                .append("name", "Vella")
                .append("restaurant_id", "41704621");
        collection.insertOne(document);
        return "success";
    }

    @RequestMapping(value = "queryAll",method = RequestMethod.GET)
    @ResponseBody
    public String queryAll() {
        final StringBuilder builder = new StringBuilder();
        long begin=new Date().getTime();

        FindIterable<Document> iterable = db.getCollection("restaurants").find();
        long end=new Date().getTime();
        System.out.println("耗时：" + (end - begin));
        final List<Document> docs=new ArrayList<Document>();
        final AtomicInteger  i=new AtomicInteger();
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                docs.add(document);
            }
        });
        end=new Date().getTime();
        System.out.println("耗时：" + (end - begin));
        System.out.println(docs.size() + ":" + i.intValue() + " : " + builder.capacity());
        return builder.toString();
    }

    @RequestMapping(value="query",method = RequestMethod.GET)
    @ResponseBody
    public String query(){
        long begin=new Date().getTime();
        FindIterable<Document> iterable;
        iterable=db.getCollection("restaurants").find(new Document("grades.score",new Document("$gt",30))).limit(10).skip(10);
        long end=new Date().getTime();
        System.out.println("耗时：" + (end - begin));

        final  StringBuilder builder=new StringBuilder();
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                builder.append(document.toJson());
            }
        });
        end=new Date().getTime();
        System.out.println("耗时：" + (end - begin));
        return builder.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(MongoController.class, args);
    }
}
