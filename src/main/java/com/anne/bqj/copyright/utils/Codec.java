/**
* @Title: codec.java 
* @Package com.bqj.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 杨智浩 yangzhihao@anne.com.cn   
* @date 2017年8月4日 下午2:24:32 
* @version V1.0
*/
package com.anne.bqj.copyright.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author 郑海龙
 *
 */
public class Codec {
	public static String EncodeBytes(byte[] in)
	{
		String out = Hex.encodeHexString(in);
		return out;
	}
	public static byte [] DecodeString(String in)
	{
		byte [] out = null;
		try {
			out = Hex.decodeHex(in.toCharArray());
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	public static String EncodeToNet(String strin)
	{
		String strout = null; 
		String strtmp = URLEncoder.encode(strin);;
		byte [] senddata = Base64.encodeBase64(strtmp.getBytes());
		strout = new String(senddata);
		return strout;
	}
	
	public static String DecodeToNet(String strin)
	{
		byte[] decode_src = Base64.decodeBase64(strin);
		String strout = URLDecoder.decode(new String(decode_src));
		return strout;
	}
}


