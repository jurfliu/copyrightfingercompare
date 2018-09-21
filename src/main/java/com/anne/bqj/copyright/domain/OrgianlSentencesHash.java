package com.anne.bqj.copyright.domain;


import java.util.Date;

/**
 * 
 * @author 郑海龙
 * elseticsearch 关于表orgianl_sentences_hash的描述,集成在ESMonitorRecordDomain表里
 */

public class OrgianlSentencesHash {

	private String hash_id;


	private String sentence;

	private Date update_time;

	private int version;

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
