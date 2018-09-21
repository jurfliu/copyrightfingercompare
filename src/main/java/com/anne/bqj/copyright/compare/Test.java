package com.anne.bqj.copyright.compare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bqj on 2018/8/3.
 */
public class Test {
    private static Logger logger = LogManager.getLogger(Test.class);
    public static void main(String args[]){
        String s="[59d893a659fa5fca44a4893309e29e90, ，布雷,克里正试,图将品牌,国际化, 2018-06-22 00:53:00.749, 1]";
       Object obj[]= getListByString(s);
       for(int m=0;m<obj.length;m++){
           System.out.println(m+" "+obj[m]);
       }
   logger.info("zhixing over!");



    }
    public static Object[]  getListByString(String content ){
        Object obj[]=new Object[4];
        if(content.startsWith("[")&&content.endsWith("]")){
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
}
