/**
 * @Title: webpage_sentences_hash.java
 * @Package com.bqj.finger
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 杨智浩 yangzhihao@anne.com.cn
 * @date 2017年8月3日 下午3:43:39
 * @version V1.0
 */
package com.anne.bqj.copyright.domain;

import java.util.Date;

/**
 * @author 郑海龙
 * elseticsearch 关于表webpage_sentences_hash的描述,集成在ESWebPageDomain表里
 *
 */
public class WebpageSentencesHash {
    private String hash_id;//句子hash，也是主键
    private String sentence;//分句后的句子
    private Date update_time;//修改时间
    private int version;//版本,暂时业务还不需要

    public String getHash_id() {
        return hash_id;
    }

    public void setHash_id(String hash_id) {
        this.hash_id = hash_id;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}





