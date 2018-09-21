package com.anne.bqj.copyright.compare;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.HashMap;


/**
 * Created by bqj on 2018/7/26.
 */
public class MyHiveUdf extends UDF {
    private static Logger logger = LogManager.getLogger(MyHiveUdf.class);
    private static HashMap<String,String> countryMap = new HashMap();
    Text text=new Text();
    //将英文转换成中文
    public  int evaluate(Text txt){
        if(txt==null){
            txt.set("");
        }

        String str=txt.toString();
        logger.info(""+str);
        return str.length();

    }
    public static void main(String args[]){
        MyHiveUdf myudf=new  MyHiveUdf();
        Text t=new Text();
        t.set("liu");
        int s=myudf.evaluate(t);
        System.out.println(s);

    }
}
