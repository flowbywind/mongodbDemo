package com.bj58.bootweb.Controller;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.javafx.scene.layout.region.BackgroundImage;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.Doc;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;
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
    private StringBuilder builder;

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
                .append("name", "Juni")
                .append("restaurant_id", "41704621");
        collection.insertOne(document);
        return "success";
    }

    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ResponseBody
    public String queryAll() {
        final StringBuilder builder = new StringBuilder();
        long begin = new Date().getTime();

        FindIterable<Document> iterable = db.getCollection("restaurants").find();
        long end = new Date().getTime();
        System.out.println("耗时：" + (end - begin));
        final List<Document> docs = new ArrayList<Document>();
        final AtomicInteger i = new AtomicInteger();
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                docs.add(document);
            }
        });
        end = new Date().getTime();
        System.out.println("耗时：" + (end - begin));
        System.out.println(docs.size() + ":" + i.intValue() + " : " + builder.capacity());
        return builder.toString();
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public String query() {
        long begin = new Date().getTime();
        FindIterable<Document> iterable;
        //iterable=db.getCollection("restaurants").find(new Document("grades.score",new Document("$gt",30))).limit(10).skip(10);

        //与查询
        //iterable=db.getCollection("restaurants").find(new Document("cuisine","Italian").append("address.zipcode","10075"));
        //iterable=db.getCollection("restaurants").find(and(eq("cuisine","Italian"),eq("address.zipcode","10075")));

        //或条件
        //iterable=db.getCollection("restaurants").find(new Document("$or",asList(new Document("cuisine","Italian")
        // ,new Document("address.zipcode", "10075"))));
        //iterable = db.getCollection("restaurants").find(or(eq("cuisine", "Italian"), eq("address.zipcode", "10075")));

        //排序
//        iterable = db.getCollection("restaurants").find(new Document("cuisine", "Italian")).sort(new Document("borough", 1).append("address.zipcode", 1));
//        iterable = db.getCollection("restaurants").find(eq("cuisine", "Italian")).sort(ascending("borough", "address.zipcode"));

        //查询
        //iterable = db.getCollection("restaurants").find(eq("name", "Juni"));
        //iterable = db.getCollection("restaurants").find(eq("restaurant_id","41156888"));
//        iterable=db.getCollection("restaurants").find(or(eq("address.zipcode","10016"),eq("cuisine","Other")));
//        iterable=db.getCollection("restaurants").find(eq("restaurant_id","4170462111"));
//        iterable = db.getCollection("restaurants").find(eq("borough", "Manhattan"));
        iterable = db.getCollection("restaurants").find(new Document());
        long end = new Date().getTime();
        System.out.println("查询耗时：" + (end - begin));

        final StringBuilder builder = new StringBuilder();
        final AtomicInteger i = new AtomicInteger();
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                builder.append(document.toJson());
                i.set(i.intValue() + 1);
            }
        });
        end = new Date().getTime();
        System.out.println("总计查询耗时：" + (end - begin) + " 条数:" + i.intValue());
        return builder.toString();
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    @ResponseBody
    public String update() {
        MongoCollection<Document> mycollection = db.getCollection("restaurants");
//        mycollection.updateMany(new Document("name", "Juni")
//                , new Document("$set", new Document("cuisine", "American(New)"))
//                        .append("$currentDate", new Document("lastModified", true)));
//       UpdateResult result = mycollection.updateOne(eq("restaurant_id","41156888"),
//                                new Document("$set",new Document("address.street","Est 31st Street")));

//         UpdateResult result=mycollection.updateMany(and(eq("address.zipcode","10016"),eq("cuisine","Other")),
//                 new Document("$set",new Document("cuisine","Category To Be Determined"))
//                 .append("$currentDate",new Document("lastModified",true)));
        UpdateOptions o = new UpdateOptions();
        o.upsert(true);
        UpdateResult result = mycollection.replaceOne(new Document("restaurant_id", "4170462111"),
                new Document("address",
                        new Document()
                                .append("street", "2 Avenue")
                                .append("zipcode", "10075")
                                .append("building", "1480")
                                .append("coord", asList(-73.9557413, 40.7720266)))
                        .append("name", "Vella 2")
                        .append("restaurant_id", "4170462111"), o);

        return result.toString();
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    public String delete() {
        long begin = new Date().getTime();
        MongoCollection<Document> myCollection = db.getCollection("restaurants");
        //删除一个的效果
//        DeleteResult result = myCollection.deleteOne(eq("restaurant_id","4170462111"));
//        DeleteResult result=myCollection.deleteOne(or(eq("address.zipcode","10016"),eq("cuisine","Other")));
//        DeleteResult result=myCollection.deleteMany(eq("borough","Manhattan"));
//        DeleteResult result = myCollection.deleteMany(new Document());
        //删除集合
        myCollection.drop();
        long end = new Date().getTime();
        System.out.println("删除耗时：" + (end - begin));

        return "";
    }

    @RequestMapping(value = "/aggregation", method = RequestMethod.GET)
    @ResponseBody
    public String aggregation() {
        final StringBuilder builder = new StringBuilder();
        long begin = new Date().getTime();
        MongoCollection<Document> myCollection = db.getCollection("restaurants");
//        AggregateIterable<Document> iterable=myCollection.aggregate(asList(new Document("$group",
//                new Document("_id","$borough")
//                        .append("count",new Document("$sum",10)))));

        AggregateIterable<Document> iterable = db.getCollection("restaurants").aggregate(asList(
                new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
                new Document("$group", new Document("_id", "$address.zipcode").append("count", new Document("$sum", 1)))));
        long end = new Date().getTime();
        System.out.print("aggregation耗时" + (end - begin) + "   **");
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                builder.append(document.toJson() + "<br/>");
            }
        });
        System.out.println("转换耗时" + (new Date().getTime() - end));
        return builder.toString();
    }

    @RequestMapping(value = "/createindex", method = RequestMethod.GET)
    @ResponseBody
    public String createIndex() {
        MongoCollection<Document> myCollection = db.getCollection("restaurants");
        String result = myCollection.createIndex(new Document("cuisine", 1));
        System.out.print(result);
        //创建复合索引
        result=myCollection.createIndex(new Document("cuisine",1).append("address.zipcode",-1));

        return result;
    }

    public static void main(String[] args) {
        SpringApplication.run(MongoController.class, args);
    }
}
