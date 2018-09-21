package com.anne.bqj.copyright.compare;

import com.anne.bqj.copyright.domain.OrgianlSentencesHash;
import com.anne.bqj.copyright.domain.WebpageSentencesHash;
import com.anne.bqj.copyright.utils.Codec;
import com.anne.bqj.copyright.utils.SentenceHashAlg;
import com.anne.bqj.copyright.utils.SimHashAlg;
import com.anne.bqj.copyright.utils.TextFilter;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;

import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bqj on 2018/8/8.
 */
public class ListCompare  extends GenericUDF {
    private static Logger logger = LogManager.getLogger(ListCompare.class);
    private transient ListObjectInspector webListOI;//webList
    private transient ListObjectInspector monitorListOI;//monitorList
    public ListCompare() {
    }
    /**
     * 初始化参数以及验证
     * @param arguments
     * @return
     * @throws UDFArgumentException
     */
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 2) {
            throw new UDFArgumentLengthException("参数的个数不正确，参数的形式为:id1，List<T1>,id2,List<T2>");
        }
        // 1. 检查是否接收到正确的参数类型
        ObjectInspector webHashList = arguments[0];//拆分成短句的样本指纹
        ObjectInspector monitorHashList = arguments[1];//拆分成短句的待验证指纹
        if (!(webHashList instanceof ListObjectInspector) ) {
            throw new UDFArgumentException("first argument must be a string, second argument must be a list / array");
        }
        if (!(monitorHashList instanceof ListObjectInspector) ) {
            throw new UDFArgumentException("third argument must be a string, fourth argument must be a list / array");
        }
        //2.获取值
        this.webListOI = (ListObjectInspector) webHashList;
        this.monitorListOI = (ListObjectInspector) monitorHashList;
        if(webListOI==null||monitorListOI==null){
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


        //3.业务处理
        try{
            // 3.1 需要比对网页的指纹数据中的分句hash，存入 webFingerHashList 中
            List<String> webFingerHashList = new ArrayList<String>();
            int arrayLength = this.webListOI.getListLength(arguments[0].get());
            logger.info("webListOI的长度:"+arrayLength);
            if (arrayLength > 0) {
                for(int i = 0; i < arrayLength; ++i) {
                    Object   listElement = this.webListOI.getListElement(arguments[0].get(), i);
                    // System.out.println("object:"+listElement);
                    Object obj[]= TextFilter.getListByString(listElement.toString());
                    if(obj!=null&&obj.length>0){
                        webFingerHashList.add(obj[0].toString());
                    }
                }
            }
            //3.2 原创文的指纹数据中的分句hash，存入 monitorFingerHashList 中
            List<String> monitorFingerHashList = new ArrayList<String>();
            int monitorArrayLength = this.monitorListOI.getListLength(arguments[1].get());
            logger.info("webListOI的长度:"+monitorArrayLength);
            if (monitorArrayLength> 0) {
                for(int i = 0; i < monitorArrayLength; ++i) {
                    Object   listElement = this.monitorListOI.getListElement(arguments[1].get(), i);
                    //System.out.println("object:"+listElement);
                    Object obj[]= TextFilter.getListByString(listElement.toString());
                    if(obj!=null&&obj.length>0){
                        monitorFingerHashList.add(obj[0].toString());
                    }
                }
            }
            //3.5 相同句子百分比，先判断短句，后整篇
            int sentenceResult = sentenceHash.Diff2Sentences(webFingerHashList, monitorFingerHashList, diff_src, diff_des, sameList);
            System.out.println("短句hash比对返回值:"+sentenceResult);
            // 3.7 如果满足侵权条件，进行侵权记录存储
            if (sentenceResult>10) {
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
    }
}
