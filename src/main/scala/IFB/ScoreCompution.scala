package IFB

import IFB.domain.{BaseVal, Business, Province}
import IFB.utils.LogUtil
import org.apache.spark.rdd.RDD

/**
  * Created by Administrator on 2017/8/29.
  */
class ScoreCompution extends Serializable {
  var GWeight = 1.0 //日期衰减权重，由于综合计算评分时调用的aggregateByKey中不方便带入变量，所以定义为全局变量，在由传入的参数赋值

  /**
    * 计算评分
    *
    * @param rdd          :RDD[(String, (String, Map[String, Double]))] 当天统计数据
    * @param rddBefore    :RDD[(String, (String, Double, String, Int))] 以往统一数据
    * @param baseValList  : java.util.List[BaseVal], 定义的基本变量列表
    * @param businessList : java.util.List[Business], 要求的业务列表
    * @param provinceList : java.util.List[Province], 要求的省份列表
    * @param savePath     : String, 分数存储的 目录
    * @param rangeDay     : Int, 计算综合评分需要考虑的范围天数
    * @param gWeight      : Double 日期衰减权重
    * @return RDD[(String, (String, Double, String))]
    */
  def getComplexScore(rdd: RDD[(String, (String, Map[String, Double]))],
                      rddBefore: RDD[(String, (String, Double, String, Int))],
                      baseValList: java.util.List[BaseVal],
                      businessList: java.util.List[Business],
                      provinceList: java.util.List[Province],
                      savePath: String,
                      rangeDay: Int,
                      gWeight: Double
                     )
  : RDD[(String, (String, Double, String))] = {

    var totalRdd: RDD[(String, String, Double, String)] = null

    GWeight = gWeight
    //计算当天分数
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][start to into compute score module]")
    var resultRDD: RDD[(String, (String, Double, String))] = null //返回值
    var allRDD: RDD[(String, (String, Double, String, Int))] = null //当日和以往分数rdd的合并rdd
    var scoreRDD: RDD[(String, String, Double, String)] = null //记录当日分数rdd

    //都为null 则退出
    if (rdd == null && rddBefore == null) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][one data and before data is null,System.exit(1)]")
      System.exit(1)
    } else {
      // 以往数据 rddBefore 不为null 则 保存至allRDD
      if (rddBefore != null)
        allRDD = rddBefore
      // 当日rdd 不为空 则计算当天评分
      if (rdd != null) {


        for (x <- 0 until businessList.size()) {
          scoreRDD = rdd.map(o => computeTodayScore(o, baseValList, businessList.get(x)))
          if (null == totalRdd)
            totalRdd = scoreRDD
          else
            totalRdd = totalRdd.union(scoreRDD)
        }
        //保存当天评分数据
        //v1.1不再进行每天的数据存储
        //scoreRDD.repartition(1).saveAsTextFile(savePath) //重新分区减少存储文件的个数
        //LogUtil.printLog(this.getClass.getSimpleName,"[IFB][Computing score of oneDate is success and saved in HDFS:" + savePath + ".]")
        //如果都不为null  则合并当日和以往数据。

        if (allRDD != null)
          allRDD = allRDD.union(totalRdd.map(o => (o._1, (o._2, o._3, o._4, 1))))
      }

      //如果之前的不为null则allRDD不空，则需要进行综合评分。
      if (allRDD != null) {
        resultRDD = allRDD.aggregateByKey(("", 0.0, "", 0))(seq, comb).map(o => {
          val score = o._2._2 / rangeDay
          (o._1, (o._2._1, score, o._2._3))
        })
      } else {
        //如果之前的为null则allRDD为空，则用scoreRDD转为对的格式直接返回，不需要进行综合评分。
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB][Before data is null, so only score of one data.]")
        resultRDD = scoreRDD.map(o => (o._1, (o._2, o._3, o._4)))
      }
    }
    //综合评分
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][Computing score module is end and will go to next step]")
    resultRDD
  }

  /**
    * aggregateByKey的seqOp函数，各天分数的衰减
    *
    * @param out : ( String, Double, String, Int) 转换后的记录
    * @param in  :( String, Double, String, Int) 传入的一条记录
    * @return : ( String, Double, String, Int)
    */
  def seq(out: (String, Double, String, Int), in: (String, Double, String, Int)): (String, Double, String, Int) = {
    val score = in._2 / Math.pow(in._4, GWeight) //对各天评分按日期对衰减
    (in._1, score, in._3, in._4)
  }

  /**
    * aggregateByKey的combOp函数，计算各天评分的均值，并确定该记录的标签
    *
    * @param in1 :( String, Double, String, Int)
    * @param in2 :( String, Double, String, Int)
    * @return :( String, Double, String, Int)
    */
  def comb(in1: (String, Double, String, Int), in2: (String, Double, String, Int)): (String, Double, String, Int) = {
    //合并各天评分， 选择最近的业务标签，及对应日期
    val score = in1._2 + in2._2
    var tag = ""
    var day = 0
    if (in1._4 < in2._4) {
      if (in1._3.equals("")) {
        tag = in2._3
        day = in2._4
      } else {
        tag = in1._3
        day = in1._4
      }
    } else {
      if (in2._3.equals("")) {
        tag = in1._3
        day = in1._4
      } else {
        tag = in2._3
        day = in2._4
      }
    }
    (in1._1, score, tag, day)
  }

  /**
    * 计算当日评分
    *
    * @param o           :(String, (String, Map[String, Double]))
    * @param baseValList : java.util.List[BaseVal],
    * @param business    : Business
    * @return (String, String, Double, String)
    */
  def computeTodayScore(o: (String, (String, Map[String, Double])),
                        baseValList: java.util.List[BaseVal],
                        business: Business)
  : (String, String, Double, String) = {
    var score = 0.0
    //初始化评分
    var tag = "" //初始化业务标签

    //v 1.1 修改算法方案.只统计count
    //计算各公共变量的加权分数
    //    for (i <- 0 until baseValList.size()) {
    //      if (o._2._2.contains(baseValList.get(i).getVarName)) {
    //        score += Math.log1p(o._2._2(baseValList.get(i).getVarName)) * baseValList.get(i).getWeight
    //      }
    //    }
    //
    //    //计算各业务的加权评分，根据阈值打标签
    //    for (i <- 0 until businessList.size()) {
    //      if (o._2._2.contains(businessList.get(i).getBusinessTag)) {
    //        score += Math.log1p(o._2._2(businessList.get(i).getBusinessTag)) * businessList.get(i).getWeight
    //        if (o._2._2(businessList.get(i).getBusinessTag) >= businessList.get(i).getThreshold)
    //          tag += businessList.get(i).getBusinessTag
    //      }
    //    }
    //计算各业务的加权评分，根据阈值打标签
    if (o._2._2.contains(business.getBusinessTag)) {
      score += o._2._2(business.getBusinessTag) * business.getWeight
      if (o._2._2(business.getBusinessTag) >= business.getThreshold)
        tag += business.getBusinessTag
    }
    (o._1, o._2._1, score, tag)
  }
}
