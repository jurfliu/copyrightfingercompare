package com.anne.bqj.copyright.compare;

import com.anne.bqj.copyright.domain.MonitorRecordFinger;
import com.anne.bqj.copyright.domain.OrgianlSentencesHash;
import com.anne.bqj.copyright.domain.WebPageDomainFinger;
import com.anne.bqj.copyright.domain.WebpageSentencesHash;
import com.anne.bqj.copyright.utils.Codec;
import com.anne.bqj.copyright.utils.SentenceHashAlg;
import com.anne.bqj.copyright.utils.SimHashAlg;
import com.anne.bqj.copyright.utils.TextFilter;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.lazy.LazyMap;
import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaBooleanObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.*;


import org.apache.hadoop.io.IntWritable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FingerCompare extends GenericUDF {
    private static Logger logger = LogManager.getLogger(FingerCompare.class);
    private transient PrimitiveObjectInspector.PrimitiveCategory inputType;
    private final DoubleWritable resultDouble = new DoubleWritable();
    private transient StringObjectInspector webElementOI;//webId
    private transient ListObjectInspector webListOI;//webList
    private transient StringObjectInspector monitorElementOI;//monitorId
    private transient ListObjectInspector monitorListOI;//monitorList

    private final IntWritable resultInt = new IntWritable();
    private final HiveDecimalWritable resultDecimal = new HiveDecimalWritable();
    private transient PrimitiveObjectInspector argumentOI;
    private transient ObjectInspectorConverters.Converter inputConverter;
    private MapObjectInspector mapOI;
    public FingerCompare() {
    }

    /**
     * 初始化参数以及验证
     * @param arguments
     * @return
     * @throws UDFArgumentException
     */
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 4) {
            throw new UDFArgumentLengthException("参数的个数不正确，参数的形式为:id1，List<T1>,id2,List<T2>");
        }
        // 1. 检查是否接收到正确的参数类型
        ObjectInspector  webHashId = arguments[0];//样本指纹
        ObjectInspector webHashList = arguments[1];//拆分成短句的样本指纹
        ObjectInspector monitorHashId = arguments[2];//待验证指纹
        ObjectInspector monitorHashList = arguments[3];//拆分成短句的待验证指纹
        if (!(webHashList instanceof ListObjectInspector) || !(webHashId instanceof StringObjectInspector)) {
            throw new UDFArgumentException("first argument must be a string, second argument must be a list / array");
        }
        if (!(monitorHashList instanceof ListObjectInspector) || !(monitorHashId instanceof StringObjectInspector)) {
            throw new UDFArgumentException("third argument must be a string, fourth argument must be a list / array");
        }
        //2.获取值
        this.webElementOI = (StringObjectInspector) webHashId;
        this.webListOI = (ListObjectInspector) webHashList;
        this.monitorElementOI = (StringObjectInspector) monitorHashId;
        this.monitorListOI = (ListObjectInspector) monitorHashList;
        if(webElementOI==null||webListOI==null||monitorElementOI==null||monitorListOI==null){
            throw new UDFArgumentException("输入的参数不能为空，不能为null");
        }
        //3.返回类型是boolean，所以我们提供了正确的object inspector
        return PrimitiveObjectInspectorFactory.writableBooleanObjectInspector;
    }

    /**
     * 获取值，进行业务处理
     * @param arguments
     * @return
     * @throws HiveException
     */
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        //1.初始化定义一些集合
        BooleanWritable resultBoolean = new BooleanWritable();//返回值
        resultBoolean.set(false);//默认值为false
        SentenceHashAlg sentenceHash = new SentenceHashAlg();//短句hash比对
        SimHashAlg simHashAlg = new SimHashAlg();//整篇文章hash比对
        //比对文与原创文相同句子索引集合，比对文的索引集合
        List<Integer> diff_src = new ArrayList();
        //原创文与比对文相同句子索引集合，原创文的索引集合
        List<Integer> diff_des = new ArrayList();
        //相同句子数量
        List<String> sameList = new ArrayList();
        // 2.利用object inspectors从传递的对象中得到list与string
        String webHashId=webElementOI.getPrimitiveJavaObject(arguments[0].get());
        String monitorHashId=monitorElementOI.getPrimitiveJavaObject(arguments[2].get());
        logger.info("webHashId的值为:"+webHashId+" webElementOI的值为:"+monitorHashId);
        //3.业务处理
        try{
            // 3.1 需要比对网页的指纹数据中的分句hash，存入 webFingerHashList 中
            List<String> webFingerHashList = new ArrayList<String>();
            int arrayLength = this.webListOI.getListLength(arguments[1].get());
            logger.info("webListOI的长度:"+arrayLength);
        if (arrayLength > 0) {
            for(int i = 0; i < arrayLength; ++i) {
             Object   listElement = this.webListOI.getListElement(arguments[1].get(), i);
               // System.out.println("object:"+listElement);
                Object obj[]= TextFilter.getListByString(listElement.toString());
                if(obj!=null&&obj.length>0){
                    webFingerHashList.add(obj[0].toString());
                }
            }
         }
         //3.2 原创文的指纹数据中的分句hash，存入 monitorFingerHashList 中
            List<String> monitorFingerHashList = new ArrayList<String>();
            int monitorArrayLength = this.monitorListOI.getListLength(arguments[3].get());
            logger.info("webListOI的长度:"+monitorArrayLength);
            if (monitorArrayLength> 0) {
                for(int i = 0; i < monitorArrayLength; ++i) {
                    Object   listElement = this.monitorListOI.getListElement(arguments[3].get(), i);
                    //System.out.println("object:"+listElement);
                    Object obj[]= TextFilter.getListByString(listElement.toString());
                    if(obj!=null&&obj.length>0){
                        monitorFingerHashList.add(obj[0].toString());
                    }
                }
            }
            //3.3 取出比对网页的的指纹数据中的全文simhash，并由String 转成 byte[]
            byte[] webPageSimHash = Codec.DecodeString(webHashId);
            //3.4 取出原创任务的的指纹数据 MonitorRecordFinger mrf 中的全文simhash，并由String 转成 byte[]
            byte[] monitorRecordSimHash = Codec.DecodeString(monitorHashId);
            //3.5 相同句子百分比，先判断短句，后整篇
            int sentenceResult = sentenceHash.Diff2Sentences(webFingerHashList, monitorFingerHashList, diff_src, diff_des, sameList);
             //3.6 比对文网页simhash与原创文simhash的海明距离
             int sim_distance = simHashAlg.GetDistance(monitorRecordSimHash, webPageSimHash);
             logger.info("短句hash比对返回值:"+sentenceResult+" 整篇文章比对返回值:"+sim_distance);
            System.out.println("短句hash比对返回值:"+sentenceResult+" 整篇文章比对返回值:"+sim_distance);
             // 3.7 如果满足侵权条件，进行侵权记录存储
             if ((sentenceResult > 50) || ((sim_distance < 30) && (sentenceResult > 15))) {
                  resultBoolean.set(true);
             }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        //4.返回结果值
        return resultBoolean;
    }

    /**
     *该方法无关紧要，我们可以返回任何东西，但应当是描述该方法的字符串
     * @param children
     * @return
     */
    public String getDisplayString(String[] children) {

        return this.getStandardDisplayString("abs", children);
    }
    public static void main(String args[]) throws HiveException {

        String webId="57047a3b66fd896f8c7e0929b0639217";
        List<WebpageSentencesHash>  webList=new ArrayList<WebpageSentencesHash>();
        WebpageSentencesHash ws=new WebpageSentencesHash();
        ws.setHash_id("17c4747945929b09356334eff57f937e");
        ws.setSentence("萨拉·布雷克里");
        ws.setUpdate_time(new Date());
        ws.setVersion(1);
        webList.add(ws);
        String monitorId="57047a3b66fd896f8c7e0929b0639216";//7e15e12597f12b936f10fa82ece6b9d2
        OrgianlSentencesHash osh=new OrgianlSentencesHash();
        List<OrgianlSentencesHash> osList=new ArrayList<OrgianlSentencesHash>();
        osh.setHash_id("88bf33a51df7d2cf89cad93bef6722fd");
        osh.setSentence("据英国《每日邮报》报道");
        osh.setUpdate_time(new Date());
        osh.setVersion(1);
        osList.add(osh);


        FingerCompare fc=new FingerCompare();

        //fc.evaluate(webId,webList,osh,osList);
        ObjectInspector stringOI = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector listOI = ObjectInspectorFactory.getStandardListObjectInspector( stringOI);
        ObjectInspector stringOI2 = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector listOI2 = ObjectInspectorFactory.getStandardListObjectInspector(stringOI2);
        fc.initialize(new ObjectInspector[]{ stringOI,listOI,stringOI2 ,listOI2});
        Object result = fc.evaluate(new DeferredObject[]{new DeferredJavaObject(webId),new DeferredJavaObject(webList),new DeferredJavaObject(webId),new DeferredJavaObject(webList)});
        System.out.println("执行结果:"+result);
    }
}

