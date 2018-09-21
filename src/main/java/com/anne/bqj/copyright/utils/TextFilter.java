/**   
* @Title: TextFilter.java 
* @Package com.bqj.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 杨智浩 yangzhihao@anne.com.cn   
* @date 2017年8月4日 下午4:38:01 
* @version V1.0   
*/
package com.anne.bqj.copyright.utils;

/**
 * @author 郑海龙
 *
 */
public class TextFilter {
	public static String FilterHtml(String in)
	{
		  /** 删除普通标签  */
		in = in.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "");
		  /** 删除转义字符 */
		in = in.replaceAll("&.{2,6}?;", "");
		
		
		/** 走一下，过滤不可见字符**/
		in = FiltersString(in);
		return in;
	}

	/**
	 *封装成数组
	 * @param content
	 * @return
	 */
	public static Object[]  getListByString(String content ){
		Object obj[]=null;
		if(content.startsWith("[")&&content.endsWith("]")){
			obj=new Object[4];
			content=content.replace("[","").replace("]","");
			String contentsArray[]=content.split(",");
			obj[0]=contentsArray[0];
			String subContent=content.substring(0,content.lastIndexOf(","));
			content=content.substring(content.indexOf(",")+1,subContent.lastIndexOf(","));
			obj[1]=content;
			obj[2]=contentsArray[contentsArray.length-2];
			obj[3]=contentsArray[contentsArray.length-1];
		}
		return obj;
	}
	
	public static String FiltersString(String in)
	{
		/**
		 * 删除不可见字符
		 */
		in = in.replace("[\\n|\\r| |\\s+]","");
		return in;
	}
}


