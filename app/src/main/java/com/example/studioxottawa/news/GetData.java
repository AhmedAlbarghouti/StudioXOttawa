package com.example.studioxottawa.news;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GetData {

    private static final String TAG = "GetData";

    /**
     * @param html
     * @return ArrayList<Article> articles
     */
    public static ArrayList<News> spiderArticle(String html) {
        ArrayList<News> newsList = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Elements elements = document
                .select("main[class=clearfix ]");


        Log.i(TAG, "spiderArticle: elements " + elements.html());

        for (Element element : elements) {
//            String title = element.select("div[class=fusion-post-content post-content]")
//                    .select("h2[class=entry-title fusion-post-title]")
//                    .select("a")
//                    .text();
            List title = element
                    .select("h2[class=entry-title fusion-post-title]")
                    .eachText();

            List description = element
                    .select("div[class=fusion-post-content-container]")
                    .eachText();
//            Log.i(TAG, "gyc " + description);
//
            List link = element
                    .select("div[class=fusion-post-content post-content]")
                    .select("h2[class=entry-title fusion-post-title]")
                    .select("a")
                    .eachAttr("href");

            Log.i(TAG, "gyc " + link);
            List date = element
                    .select("div[class=fusion-alignleft]")
                    .select("span[class=updated rich-snippet-hidden]")
                    .eachText();

//            Log.i(TAG, "gyc " + date.get(0).toString().substring(0,10));
//                String articleUrl = element
//                        .select("div[class=z-feed-img ]")
//                        .select("a")
//                        .attr("href");
            for(int j=0; j<title.size(); j++){
                News news = new News(title.get(j).toString(), description.get(j).toString(),
                        link.get(j).toString(),date.get(j).toString().substring(0,10));
                newsList.add(news);
            }
//            News news = new News(title.get(0).toString(), description.get(0).toString(),
//                    link.get(0).toString(),date.get(0).toString().substring(0,10));
//            newsList.add(news);
//            String test = newsList.get(0).getTitle();
//            Log.i(TAG, "gyc " + test);
            //Log.e("DATA>>",article.toString());
        }
        return newsList;
    }
}
