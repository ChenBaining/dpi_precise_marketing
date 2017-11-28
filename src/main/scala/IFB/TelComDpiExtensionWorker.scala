package IFB

import java.util.Date

import IFB.domain._
import IFB.utils.{ConfigUtils, _}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._

/**
  * Created by CainGao on 2017/8/30.
  */
object TelComDpiExtensionWorker {

  def main(args: Array[String]): Unit = {
    //获取当前启动的时间.
    val hour = DateUtil.getCurrentHour
    val toDayFormat = DateUtil.dateFormat(new Date)
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][开始任务------------------------------------------------------------]")
    //获取args中的配置路径,读取json配置文件,转换为Entrance对象
    if (args.length > 1) {
      ConfigUtils.setConfigPath(args(0))
      ConfigUtils.setUrlConfigPath(args(1))
    } else {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][没有传配置文件的路径过来或配置文件的数量不够!]")
      System.exit(1)
    }
    val conf = new SparkConf().setAppName("UserRecommender")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", "IFB.utils.DpiRegistrator")
    val sc = new SparkContext(conf)

    //清理历史文件
    val historyFileClean = new HdfsHistoryFileCleanUp
    historyFileClean.clear(ConfigUtils.getDpiConfig)

    //数据过滤
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][开始过滤------------------------------------------------------------]")
    val dataFilter = new DataFilter()
    val urlRDD = dataFilter.filterDataEntrance(sc, ConfigUtils.getUrlConfig, ConfigUtils.getDpiConfig)

    //数据统计
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][开始统计------------------------------------------------------------]")
    val dataCount = new DataCount
    val out = dataCount.getCountsByDataSource(urlRDD)

    //计算评分
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][开始评分------------------------------------------------------------]")
    val scoreRDD = scoreCompution(sc, out, ConfigUtils.getDpiConfig, toDayFormat)

    //提取推荐用户
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][开始推荐------------------------------------------------------------]")
    //直接把所有的数据返回到这里?     要推荐的用户直接返回生成一个RDD   ｛key，value【用户|tags^用户|tags】｝
    val pushUserRDD = userRecomWorker(sc, scoreRDD, ConfigUtils.getDpiConfig, toDayFormat, hour)



    //获取token,广播变量
    //    LogUtil.printLog(this.getClass.getSimpleName,"[IFB][开始推送最终获取到的用户到获客平台------------------------------------]")
    //    val telcomToken = new TelcomToken
    //    val tokenId = telcomToken.getToken(ConfigUtils.getDpiConfig.getTelcomGainCustomer)
    //    var token = sc.broadcast(tokenId)
    //    val telcomGainCustomer = ConfigUtils.getDpiConfig.getTelcomGainCustomer
    //    //foreachPartation
    //    pushUserRDD.cache()
    //    /**
    //      * 分批推送用户
    //      */
    //    val pushUserSize = pushUserRDD.count()
    //    urlRDD.unpersist()
    //    LogUtil.printLog(this.getClass.getSimpleName,"[IFB][推送到获客平台的最终用户数量为:" + pushUserSize)
    //    val pushBatchSize = ConfigUtils.getDpiConfig.getTelcomGainCustomer.getPushBatchSize
    //    var forsize = if (pushUserSize % pushBatchSize > 0) pushUserSize / pushBatchSize + 1 else pushUserSize / pushBatchSize
    //    val intervalTimes = ConfigUtils.getDpiConfig.getTelcomGainCustomer.getIntervalTime
    //    var startTime = new Date()
    //    val pushUserZipRDD = pushUserRDD.zipWithIndex()
    //
    //    for (i <- 0l to forsize - 1) {
    //      //计算已经运行的间隔时间
    //      val intervalTime = DateUtil.intervalMin(startTime, new Date())
    //      if (intervalTime > intervalTimes) {
    //        val newTokenId = telcomToken.getToken(ConfigUtils.getDpiConfig.getTelcomGainCustomer)
    //        LogUtil.printLog(this.getClass.getSimpleName,"[IFB][已超时\t" + intervalTime + " \t重新获取token:\t" + newTokenId + "]")
    //        startTime = new Date()
    //        token.unpersist()
    //        token = sc.broadcast(newTokenId)
    //      }
    //      pushUserZipRDD.filter(rec => {
    //        (rec._2 >= (i * pushBatchSize) && rec._2 <= (i + 1) * pushBatchSize)
    //      }).map(rec => {
    //        rec._1
    //      }).mapPartitions(rec => {
    //        rec.map(rec => {
    //          telcomToken.sendRecomUser(telcomGainCustomer, token.value, rec._1, rec._2)
    //        })
    //      }).count()
    //    }
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][结束任务------------------------------------------------------------]")
    sc.stop()
  }

  /**
    * 根据统计数据，计算综合评分
    *
    * @param sc      :SparkContext
    * @param rdd     :RDD[(String, Map[String,Int])] 当日统计数据
    * @param conf    :DpiConfig 配置参数
    * @param oneDate :String 计算数据所属日期
    * @return :RDD[(String,String)] 统计分数
    */
  def scoreCompution(sc: SparkContext, rdd: RDD[(String, (String, Map[String, Double]))], conf: DpiConfig, oneDate: String): RDD[(String, (String, Double, String))] = {
    var finalScoreRDD: RDD[(String, (String, Double, String))] = null //初始化返回值
    //上一步的数据为null，则日志警告，并继续进行
    if (rdd == null) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][本次统计数据为null,程序直接退出！]")
      System.exit(1)
    }
    val savePath = conf.getScoreStoragePath + "/" + oneDate //当日评分储存路径
    if (HdfsUtil.checkSavePath(savePath)) LogUtil.printLog(this.getClass.getSimpleName, "[IFB][当日评分存储目录已存在，故删除！]")
    //检查存储路径，已有则删除
    //    val rddBefore = getScoreBefore(sc, oneDate, conf.getRangeDay, conf.getScoreStoragePath, conf.getProvinceList)//获取以往评分数据
    // v1.1不再进行以往评分的计算.
    val rddBefore = null
    finalScoreRDD = new ScoreCompution().getComplexScore(rdd, rddBefore, conf.getBaseValList, conf.getBusinessList, conf.getProvinceList, savePath, conf.getRangeDay, conf.getgWeight())
    finalScoreRDD
  }

  /**
    * 获取n天前的评分数据
    *
    * @param sc               :SparkContext
    * @param oneDate          :给定日期
    * @param rangeDay         :计算综合评分考虑的范围天数
    * @param scoreStoragePath :分数存储的根目录
    * @param provinceList     :需要计算的省份
    * @return :RDD[(String,(Double,String,Int))]  可能为null
    */
  def getScoreBefore(sc: SparkContext, oneDate: String, rangeDay: Int, scoreStoragePath: String, provinceList: java.util.List[Province]): RDD[(String, (String, Double, String, Int))] = {
    var allBeforeRDD: RDD[(String, (String, Double, String, Int))] = null //以往的评分数据
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][开始读取以往的评分数据]")
    if (rangeDay > 0) {
      //获取省份的简称
      val provinceNiceList: java.util.List[String] = new java.util.ArrayList[String]()
      for (i <- 0 until provinceList.size()) {
        provinceNiceList.add(provinceList.get(i).getProvinceNick)
      }
      //循环读入多天以往数据
      for (i <- 1 to rangeDay) {
        val date = DateUtil.getBeforeDateFormOneDate(oneDate, i)
        //获取对应日期
        val datePath = scoreStoragePath + "/" + date //构建文件地址
        if (HdfsUtil.exists(datePath)) {
          //判断是否存在
          //读取文件，映射为正确的格式，根据省份过滤
          val rdd = sc.textFile(datePath).map(o => {
            val arr = o.substring(1, o.length - 1).split(",", -1)
            (arr(0), (arr(1), arr(2).toDouble, arr(3), i + 1))
          }).filter(o => {
            provinceNiceList.contains(o._2._1)
          })
          allBeforeRDD = if (allBeforeRDD == null) rdd else allBeforeRDD.union(rdd)
        } else LogUtil.printLog(this.getClass.getSimpleName, "[IFB][ " + date + " 的评分数据不存在！]")
      }
    } else if (rangeDay == 0) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][以往日期范围= " + rangeDay + "]")
    } else {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][以往日期范围= " + rangeDay + ", 是非法的!]")
      System.exit(1)
    }
    if (allBeforeRDD == null)
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][以往历史数据为null]")
    allBeforeRDD
  }

  /**
    * 基于档次统计的用户得分结果,获得到最终需要推荐的用户列表
    *
    * @param sc           :SparkContext
    * @param toDayUserRDD :RDD[(String, (String, Double, String))]
    * @param dpiConf      :DpiConfig
    */
  def userRecomWorker(sc: SparkContext, toDayUserRDD: RDD[(String, (String, Double, String))], dpiConf: DpiConfig, toDayFormat: String, hour: String): RDD[(String, String)] = {
    if (null == toDayUserRDD) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][今天获取到的数据为空,无法进行处理,直接退出!")
      System.exit(1)
    }
    /**
      * 1.判断程序执行条件.
      * 2.根据执行条件获取文件读取方式.
      * 3.循环方式获取分类, 如 条件为 0 则执行分类方式,则有多条. 条件为1 则只有1条.
      * 4.根据推送时间范围读取到该类目下应该读取的文件.
      */
    //val path = dpiConf.getUserStoragePath
    //当== 0 时候,进行分业务执行
    val provinces = dpiConf.getProvinceList.asScala
    var pushUserRDD: RDD[(String, String)] = null
    if (dpiConf.getUserProcessLogic.equals("0")) {
      // 当业务等于0.同样是按照各个省份来进行判断,只是不同的是.每个省份下面都有不同的业务需要进行处理.
      for (province: Province <- provinces) {
        //该省份下的业务列表
        val userRecomes = province.getUserRecomBusinessList.asScala
        //获取到该省份的RDD
        for (userRecom: UserRecomBusiness <- userRecomes) {
          // 获取到该省份下的业务的RDD.
          val busniessRDD = toDayUserRDD.filter(line => {
            line._2._1.equalsIgnoreCase(province.getProvinceNick) && line._2._3.contains(userRecom.getBusinessTag)
          })
          val userRDD = userWorkerProcessor(hour, toDayFormat, dpiConf, dpiConf.getUserStoragePath, busniessRDD, userRecom, province, sc)
          pushUserRDD = if (pushUserRDD == null) userRDD else pushUserRDD.union(userRDD)
        }
      }

    } else {
      // 当业务等于1.同样是按照各个省份来进行判断,只是不同的是.每个省份下面的所有页面都不进行判断.不进行处理.
      val userRecom = dpiConf.getUserBaseRecomBusiness
      for (province: Province <- provinces) {
        val busniessRDD = toDayUserRDD.filter(line => {
          line._2._1.equalsIgnoreCase(province.getProvinceNick)
        })
        val userRDD = userWorkerProcessor(hour, toDayFormat, dpiConf, dpiConf.getUserStoragePath, busniessRDD, userRecom, province, sc)
        pushUserRDD = if (pushUserRDD == null) userRDD else pushUserRDD.union(userRDD)
      }
    }
    pushUserRDD
  }

  /**
    * 获取到基本信息目录，
    *
    * @param basefile     :String
    * @param toDayUserRDD :RDD[(String, (String, Double, String))]
    * @param userRecomBus :UserRecomBusiness
    */
  def userWorkerProcessor(hour: String, toDayFormat: String, dpiConfig: DpiConfig, basefile: String, toDayUserRDD: RDD[(String, (String, Double, String))], userRecomBus: UserRecomBusiness, province: Province, sc: SparkContext): RDD[(String, String)] = {
    var historyRdd: RDD[(String)] = null
    for (i <- 0 to 24) {
      val hourFormat = if (i / 10 < 1) "0" + i else i + ""
      val path = UserRecommenderUtil.paths(basefile, userRecomBus.getRangeDay, hourFormat, province.getProvinceNick, userRecomBus.getBusinessTag)
      if (path != null && !path.equals("") && path.length > 0) {
        try {
          val fileRDD = sc.textFile(path)
          historyRdd = if (historyRdd == null) fileRDD else historyRdd.union(fileRDD)
        } catch {
          case ex: Exception => ""
        }
      }
    }
    val userByProvinceRecomWorker = new UserByProvinceRecomWorker
    if (null == historyRdd) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][{" + province.getProvinceName + "}省,{" + userRecomBus.getBusinessTag + "}业务,{}没有获取到历史推荐用户]")
      userByProvinceRecomWorker.userRecommender(hour, toDayFormat, dpiConfig, basefile, province, userRecomBus, toDayUserRDD, null)
    } else {
      userByProvinceRecomWorker.userRecommender(hour, toDayFormat, dpiConfig, basefile, province, userRecomBus, toDayUserRDD, historyRdd)
    }
  }
}