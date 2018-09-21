package com.anne.bqj.copyright.domain;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 郑海龙
 */
//@Component
//@Document(collection = "webpage_finger")
public class WebPageDomainFinger {
 //  @Id
    private String id;
    private String url;//网页url
    private String simhash;//全文hash
    private Date date; //写入时间

    public List<WebpageSentencesHash> webpage_sentences_hash_list = new ArrayList();//句子hash


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSimhash() {
        return simhash;
    }

    public void setSimhash(String simhash) {
        this.simhash = simhash;
    }

    public List<WebpageSentencesHash> getWebpage_sentences_hash_list() {
        return webpage_sentences_hash_list;
    }

    public void setWebpage_sentences_hash_list(List<WebpageSentencesHash> webpage_sentences_hash_list) {
        this.webpage_sentences_hash_list = webpage_sentences_hash_list;
    }
}


