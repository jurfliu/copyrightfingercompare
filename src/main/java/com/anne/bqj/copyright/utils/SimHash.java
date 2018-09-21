/*
 * @Title: SimHash.java
 * @Package org.bqj.simhash
 * @Description: Simhash生成算法，根据原来的C++代码转换而成
 * @author liguanjun@anne.com.cn
 * @date 2017/10/19
 * @version V2.0
 */
package com.anne.bqj.copyright.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 该类用于生成根据simhash算法，为一个文本生成simhash，用于文本之间的比较，根据simhash的海明威距离计算出文本之间的相识度
 */
public class SimHash {
 
	/**停用词字符串*/
	private static String strStopChars = "\t\r\n。、 　“”〃〈〉《》「」『』【】〒〓〔〕〖〗〝〞﹐﹑﹒﹔﹕﹖﹗﹙﹚﹛﹜﹝﹞﹟﹠﹡﹢﹣﹤﹥﹦﹨﹩﹪﹫！＂＃＄％＆＇（）＊＋，－．／：；＜＝＞？［＼］＾＿｀｛｜｝～!\"#$%&'()*+,-./:;<=>?[\\]^_`{|}~";

	/**字符串分块大小，*/
    private int iBlockSize;

    /**正态分布的数学期望，用于计算每一个文本块的权重*/
    private double dblMathExpection;
    
    public SimHash(double mathExpection, int blockSize) {
    	iBlockSize = blockSize;
    	dblMathExpection = mathExpection;
    }

    public SimHash() {
    	iBlockSize = 2;
    	dblMathExpection = 1;
    }
    
    public void setStopChars(String str) {
    	strStopChars = str;
    }
    
    public String getstrStopChars() {
    	return strStopChars;
    }
    
    /**
     * 根据文本生成simhash
     * @param text 输入的文本
     * @return 转换成的simhash，16个字节的byte数字
     * @throws UnsupportedEncodingException 
     * @throws NoSuchAlgorithmException 
     */
    public byte[] generateFingerPrinter(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String strResult = filterStopChars(text);
        return countWeight(strResult);
    }
    
    /**
     * 根据文本生成simhash
     * @param text 输入的文本
     * @return 转换成的simhash，并把16个字节的simhash转换成16进制格式
     * @throws UnsupportedEncodingException 
     * @throws NoSuchAlgorithmException 
     */
    public String generateHexFingerPrinter(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	return Hex.encodeHexString(generateFingerPrinter(text));
    }
    
    /**
     * 过滤掉不需要的特殊符号
     * @param text 要处理的文本
     * @return 返回处理后的文本字符串
     */
    protected String filterStopChars(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray())
        {
            if (c >= 0xFF01 && c <= 0xFF65)
                c -= 0xFEE0;
            if (c >= 'A' && c <= 'Z')
                c -= 32;

            if (strStopChars.indexOf(c) >= 0) continue;

            sb.append(c);
        }
        return sb.toString();
    }
    
    /**
     * 计算出一个文本的simhash
     * @param text 文本
     * @return 返回计算出的simhash
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException
     */
    protected byte[] countWeight(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        int count = text.length();
        double[] arWeight = new double[128];
        double dbl = 2 * Math.pow(dblMathExpection, 2);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        
        for (int i = 0; i < count - iBlockSize; i++) {
            double pos = 0;
            if (i < count / 2)
                pos = (count / 2 - i + iBlockSize / 2) * 4.0 / count;
            else
                pos = (i - count / 2 + iBlockSize / 2) * 4.0 /count;

            double weight = Math.exp(-Math.pow(pos, 2) / dbl);
            byte[] byVal = md5.digest(text.substring(i, i + iBlockSize).getBytes("unicode"));


            for (int j = 0; j < 16; j++)
            {
                for (int k = 0; k < 8; k++)
                {
                    if ((byVal[j] & (1 << k)) == 0)
                        arWeight[j * 8 + k] -= weight;
                    else
                        arWeight[j * 8 + k] += weight;
                }
            }
        }

        byte[] byData = new byte[16];
        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (arWeight[i * 8 + j] > 0)
                	byData[i] |= 1 << j;
                else
                	byData[i] &= ~(1 << j);
            }
        }
        
        return byData;
    }
    
    /**
     * 计算两个simhash之间的海明威距离，即计算两个simhash bit为不相同的个数
     * @param data1 第一个simhash的值
     * @param data2 第二个simhash的值
     * @return 海明威距离
     */
    public static int judgeSimilar(byte[] data1, byte[] data2)
    {
        byte c;
        int count = 0;
        for (int i = 0; i < 16; i++)
        {
            c = (byte) (data1[i] ^ data2[i]);

            count += countOne(c);
        }

        return count;
    }

    protected static int countOne(int x)
    {
        int sum = 0;
        while (x > 0)
        {
            sum++;
            x = x&(x - 1);
        }

        return sum;
    }
}
