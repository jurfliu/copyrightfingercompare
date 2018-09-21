package com.anne.bqj.copyright.domain;
/**
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;
**/
import java.util.ArrayList;
import java.util.List;

/**
 * 原创任务指纹表
 */
//@Component
//@Document(collection = "monitor_record_finger")
public class MonitorRecordFinger {

  //  @Id
    private String id;//主键id

   // @Field("simhash")
    private String simhash;//全文hash


   // @Field("orgianl_sentences_hash_list") //句子Hash
    public List<OrgianlSentencesHash> orgianl_sentences_hash_list = new ArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimhash() {
        return simhash;
    }

    public void setSimhash(String simhash) {
        this.simhash = simhash;
    }

    public List<OrgianlSentencesHash> getOrgianl_sentences_hash_list() {
        return orgianl_sentences_hash_list;
    }

    public void setOrgianl_sentences_hash_list(List<OrgianlSentencesHash> orgianl_sentences_hash_list) {
        this.orgianl_sentences_hash_list = orgianl_sentences_hash_list;
    }
}
