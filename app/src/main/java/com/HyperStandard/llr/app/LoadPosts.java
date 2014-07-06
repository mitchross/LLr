package com.HyperStandard.llr.app;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by nonex_000 on 7/5/2014.
 */
public class LoadPosts implements Callable<ArrayList<TopicPost>> {
    private String selector;
    private Document page;
    LoadPosts(String selector, Document page) {
        this.page = page;
        this.selector = selector;
    }

    @Override
    public ArrayList<TopicPost> call() throws Exception {
        Elements elements = page.select(selector);
        ArrayList<TopicPost> array = new ArrayList<>(elements.size());
        for (Element e : elements) {
            array.add(new TopicPost(e));
        }
        return array;
    }
}
