package com.anne.bqj.copyright.utils;



import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

//import org.apache.log4j.Logger;

/**
 * 
 * @author
 * 本类主要用于simhash
 */
//@Component
public class SimHashAlg {
	public static SimHashAlg simHashAlg = null;
	private SimHash simHash = new SimHash();
	public SimHashAlg()
	{
	}

	
	/**
	 * 
	* @Title: Diff2File 
	* @Description: TODO(比对两个文件返回simhash值) 
	* @param @param strSrc
	* @param @param strDes
	* @param @return    设定文件 
	* @return int    返回类型 
	* @throws
	 */
	public int Diff2File(String strSrc,String strDes)
	{
		//need lock
		int iret = 0;
		byte[] sim1 = GetSimHash(strSrc);
		byte[] sim2 = GetSimHash(strDes);
		iret = GetDistance(sim1,sim2);
		return iret;
	}
	
	public byte[] GetSimHash(String str)
	{
		byte [] out = null;
		try {
			out = simHash.generateFingerPrinter(str);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	public int GetDistance(byte [] src,byte [] des)
	{
		int iret = 0;
		iret = simHash.judgeSimilar(src,des);
		return iret;
	}
}
