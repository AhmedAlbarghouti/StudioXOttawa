package com.example.studioxottawa.news;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetText {
    private static final String TAG = "GetData";

    /**
     * @param html
     * @return ArrayList<Article> articles
     */
    public static String spiderArticle(String html) {
        ArrayList<News> newsList = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Elements elements = document
                .select("div[class=post-content ]");

        String text = "";
        Log.i(TAG, "spiderArticle: elements " + elements.html());
        Log.i(TAG, "spiderArticle: elements " + elements.size());

        for (Element element : elements) {

            List para = element
                    .select("p")
                    .eachText();

            List description = element
                    .select("a")
                    .eachText();

            Log.i(TAG, "gyc " + description);

            List link = element
                    .select("a")
                    .eachAttr("href");
            for(int k=0; k<link.size(); k++){
                if (link.get(k).toString().contains("jpg")||link.get(k).toString().contains("png")){
                    link.remove(k);
                }
            }
            Log.i(TAG, "gyc " + link);

            for(int j=0; j<para.size(); j++){
                text = text + para.get(j).toString() + "\n";
                Log.i(TAG, "gyctext " + text);
            }

            for(int j=0; j<description.size(); j++){
                text = text + description.get(j).toString() + "\n" + link.get(j) + "\n";
                Log.i(TAG, "gyctext " + text);
            }
        }

        return text;
    }
}
