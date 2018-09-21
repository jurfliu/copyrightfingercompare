
package com.anne.bqj.copyright.utils;


import com.anne.bqj.copyright.compare.MyHiveUdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.log4j.Logger;

/**
 * 
 * @author 郑海龙
 * 
 * 本类用与句子hash比对
 */
//@Component
public class SentenceHashAlg {
//	private Logger log=Logger.getLogger(SentenceHashAlg.class);
	//private Logger logger =  LoggerFactory.getLogger(this.getClass());
	final java.util.logging.Logger logger= java.util.logging.Logger.getLogger(String.valueOf( MyHiveUdf.class));
	public int Diff2Sentences(List srcHash,List desHash,List diff_list_src,List diff_list_des,List sameList)
	{
		int iret = 0;
		List same_sentence = diffSentence(srcHash, desHash, diff_list_src, diff_list_des);
		sameList.add(""+same_sentence.size());
		if (same_sentence.size() > 0) {
			if (srcHash.size() > desHash.size()) {
				//System.out.println(">"+" "+diff.size()+" "+ s2.size()+" "+s1.size());
				iret = (int) (((double) same_sentence.size() / (double) desHash.size()) * 100);
			} else {
				//System.out.println("<"+" "+diff.size()+" "+ s2.size()+" "+s1.size());
				iret = (int) (((double) same_sentence.size() / (double) srcHash.size()) * 100);
			}
		}
		return iret;
	}
	
	public int GenSentenceHash(String src,List<String> out,List<String> rawSentence)
	{
		int iret = 0;
		out.addAll(hashSentenceList(getSentenceS(formatHtml(src), rawSentence)));
		iret = out.size();
		return iret;
	}
	
	public int GenSentenceHash(String src,List<String> out)
	{
		int iret = 0;
		List<String> rawList = new ArrayList<String>();
		src = TextFilter.FilterHtml(src);
		out.addAll(hashSentenceList(getSentenceS(formatHtml(src), rawList)));
		iret = out.size();
		return iret;
	}
	
	/**
	 * 
	* @Title: replaceHtml 
	* @Description: TODO(清洗不不要的字符) 
	* @param @param html
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public String replaceHtml(String html) {
		String out = html.replaceAll("[\\n|\\r| |a-zA-Z|\"|=|:|-|<|>|/|;]", "");
		return out;
	}
	
	public int Diff2File(String src, String des, List src_list, List des_list, List diff_list_src, List diff_list_des,List sameList) {
		int iret = 0;
		/*
			src = replaceHtml(src);
			des = replaceHtml(des);
		*/
		List s1 = hashSentenceList(getSentenceS(formatHtml(src), src_list));
		List s2 = hashSentenceList(getSentenceS(formatHtml(des), des_list));
		// System.out.println(s2.size());
		List same_sentence = diffSentence(s1, s2, diff_list_src, diff_list_des);
		sameList.add(""+same_sentence.size());

		if (same_sentence.size() > 0) {
			if (s1.size() > s2.size()) {
				iret = (int) (((double) same_sentence.size() / (double) s2.size()) * 100);
			} else {
				iret = (int) (((double) same_sentence.size() / (double) s1.size()) * 100);
			}
		}
		return iret;
	}
	
	/**
	 * 
	* @Title: Diff2File 
	* @Description: TODO(比较两个两段文本的差异) 
	* @param @param src
	* @param @param des
	* @param @param src_list
	* @param @param des_list
	* @param @param diff_list_src
	* @param @param diff_list_des
	* @param @return    设定文件 
	* @return int    返回类型 返回差异的百分比
	* @throws
	 */
	public int Diff2File(String src, String des, List src_list, List des_list, List diff_list_src, List diff_list_des) {
		int iret = 0;
		List s1 = hashSentenceList(getSentenceS(formatHtml(src), src_list));
		List s2 = hashSentenceList(getSentenceS(formatHtml(des), des_list));
		List same_sentence = diffSentence(s1, s2, diff_list_src, diff_list_des);
		if (same_sentence.size() > 0) {
			if (s1.size() > s2.size()) {
				iret = (int) (((double) same_sentence.size() / (double) s2.size()) * 100);
			} else {
				iret = (int) (((double) same_sentence.size() / (double) s1.size()) * 100);
			}
		}
		return iret;
	}
	/**
	 * 
	* @Title: Diff2File 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param src
	* @param @param des
	* @param @return    设定文件 
	* @return int    返回类型 返回差异的百分比
	* @throws
	 */
	public int Diff2File(String src, String des) {
		int iret = -1;
		//src = replaceHtml(src);
		//des = replaceHtml(des);
		List s1 = hashSentenceList(getSentence(formatHtml(src)));
		List s2 = hashSentenceList(getSentence(formatHtml(des)));
		List diff = diffSentence(s1, s2);
		if (diff.size() > 0) {
			if (s1.size() > s2.size()) {
				iret = (int) (((double) diff.size() / (double) s2.size()) * 100);
			} else {	
				iret = (int) (((double) diff.size() / (double) s1.size()) * 100);
			}
		}
		return iret;
	}
	
	/**
	 * 
	* @Title: getSentence 
	* @Description: TODO(把文本拆解成一个一个的句子) 
	* @param @param html
	* @param @return    设定文件 
	* @return List    返回类型 
	* @throws
	 */
	List getSentence(String html) {
		List<String> list = new ArrayList();
		String regEx = "(.*?)[\uFF0C|\u3002 |！]";
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(html);
		while (m.find()) {
			String str = m.group();
			list.add(str);
		}
		return list;
	}
	
	/**
	 * 
	* @Title: getSentence 
	* @Description: TODO(将文本拆解为一个一个的句子) 
	* @param @param html
	* @param @param out
	* @param @return    设定文件 
	* @return List    返回类型 
	* @throws
	 */
	List getSentence(String html, List out) {
		List<String> list = new ArrayList();
		String regEx = "(.*?)[\uFF0C|\u3002 |！]";
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(html);
		logger.info("r1");
		/**
		 * todo ~~~~~
		 */
		while (m.find()) {
			String str = m.group();
			list.add(str);
			out.add(str);
		}
		logger.info("r2");
		return list;
	}
	
	public static List getSentenceS(String html,List out)
	{
		List<String> list = new ArrayList();
		int curindex = 0;
		
		for(int i =0;i<html.length();i++)
		{
			if((html.charAt(i) == '。') || (html.charAt(i) == '，') || (html.charAt(i) == '！'))
			{
				String strSentences = html.substring(curindex,i);
				curindex = i;
				if((strSentences != "") && (strSentences != "。") && (strSentences != "，") && (strSentences != "！"))
				{
					if(strSentences.length()>5)
					{
						list.add(strSentences);
						out.add(strSentences);
					}
				}
			}
		}
		if(curindex<(html.length()-1))
		{
			String strSentences = html.substring(curindex, (html.length()-1));
			list.add(strSentences);
			out.add(strSentences);
		}
		return list;
	}
	/**
	 * 
	* @Title: formatHtml 
	* @Description: TODO(清洗一些字符串) 
	* @param @param html
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	String formatHtml(String html) {
		/*
		String out = "";
		out = html.replaceAll("[\\n|\\r| ]", "");
		return out;
		*/
		return html;
	}
	
	/**
	 * 
	* @Title: hashSentenceList 
	* @Description: TODO(对句子进行hash然后返回) 
	* @param @param sentenceList
	* @param @return    设定文件 
	* @return List    返回类型 
	* @throws
	 */
	List hashSentenceList(List sentenceList) {
		List<String> out = new ArrayList();
		for (int i = 0; i < sentenceList.size(); i++) {
			String s = (String) sentenceList.get(i);
			try {
				out.add(EncoderByMd5(s));
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//	logger.error("encodebymd5 error");

			}
		}
		return out;
	}
	
	
	/**
	 * 
	* @Title: diffSentence 
	* @Description: TODO(比对两个文本句子的list的hash值的差异部分) 
	* @param @param s1
	* @param @param s2
	* @param @return    设定文件 
	* @return List    返回类型 
	* @throws
	 */

	List diffSentence(List s1, List s2) {
		List out = null;
		if (s1.size() > s2.size()) {
			out = new ArrayList(s1);
			out.retainAll(s2);
		} else {
			out = new ArrayList(s2);
			out.retainAll(s1);
		}
		return out;
	}
	/**
	 * 
	* @Title: diffSentence 
	* @Description: TODO(比对两个文本句子的list的hash值的差异部分) 
	* @param @param s1
	* @param @param s2
	* @param @param diff_s1
	* @param @param diff_s2
	* @param @return    设定文件 
	* @return List    返回类型 
	* @throws
	 */
	List diffSentence(List s1, List s2, List diff_s1, List diff_s2) {
		List out = null;
		if (s1.size() > s2.size()) {
			out = new ArrayList(s1);
			//取交集方法retainAll out 与 s2存在相同元素，out集合只保留s2中存在的元素
			out.retainAll(s2);
		} else {
			out = new ArrayList(s2);
			out.retainAll(s1);
		}
		if (out.size() != 0) {
			int out_index = 0;
			//int i 表示从第几位索引开始开始相同的，-1表示没有相同的
			int i = s1.indexOf(out.get(out_index));
			if (i != -1) {

				//循环比对集合
				for (; i < s1.size(); i++) {
					if (out_index >= out.size()) {
						break;
					}

					//如果相同，就装入diff_s1，便于以后统计，抄袭占比
					if (((String) out.get(out_index)).equals((String) s1.get(i))){
						out_index++;
						diff_s1.add(i);
					}
					//如果不同，就再比对一次，发现在集合中有，就-1索引，以让循环再次执行装入diff_s1
					else
					{
						if(out_index < out.size())
						{
							/**
							 * 应该再次判断 diff_s1里是否已经有这个数据了
							 * 如果有，则index到下一个
							 */
							i = s1.indexOf(out.get(out_index));
							i = i-1;
						}
					}
				}
			}

			out_index = 0;
			i = s2.indexOf(out.get(out_index));
			if (i != -1) {
				for (; i < s2.size(); i++) {
					if (out_index >= out.size()) {
						break;
					}
					if (((String) out.get(out_index)).equals((String) s2.get(i))) {
						out_index++;
						diff_s2.add(i);
					}
					else
					{
						if(out_index < out.size())
						{
							i = s2.indexOf(out.get(out_index));
							i = i-1;
						}
					}
				}
			}
		}
		return out;
	}
	/**
	 * 
	* @Title: EncoderByMd5 
	* @Description: TODO(md5编码) 
	* @param @param str
	* @param @return
	* @param @throws NoSuchAlgorithmException
	* @param @throws UnsupportedEncodingException    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		BASE64Encoder base64en = new BASE64Encoder();
		String out = Codec.EncodeBytes(md5.digest(str.getBytes("utf-8")));
		return out;
	}
}
