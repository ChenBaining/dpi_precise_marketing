package IFB

import java.util.Date

import IFB.domain.{DpiConfig, Province, UserRecomBusiness}
import IFB.utils.{DateUtil, HdfsUtil, LogUtil, UserRecommenderUtil}
import org.apache.spark.rdd.RDD

/**
  * Created by CainGao on 2017/9/6.
  */
class UserByProvinceRecomWorker {

  def userRecommender(hour: String, toDayFormat: String, dpiConfig: DpiConfig, userfile: String, province: Province, userRecomBus: UserRecomBusiness, toDayUserRDD: RDD[(String, (String, Double, String))], historyRDD: RDD[String]): RDD[(String, String)] = {
    //从今天的数据中把历史数据去掉
    val toDayClearRDD = removeHistoryUser(toDayUserRDD, historyRDD)
    val sortRDD = toDayClearRDD.sortBy(line => line._2._2, false)
    val withIndexRDD = sortRDD.zipWithIndex()
    //  今天的存储目录
    val todayPath = UserRecommenderUtil.pathByDayFormat(userfile, DateUtil.dateFormat(new Date), hour, province.getProvinceNick, userRecomBus.getBusinessTag)
    //检查今天的文件夹如果存在则删除
    HdfsUtil.checkSavePath(todayPath)
    val pushTelUserRDD = telUserSend(hour, toDayFormat, dpiConfig, userRecomBus, withIndexRDD, todayPath + "/tel", province.getProvinceNick)
    val pushMsgUserRDD = msgUserSend(hour, toDayFormat, dpiConfig, userRecomBus, withIndexRDD, todayPath + "/msg", province.getProvinceNick)
    pushTelUserRDD.union(pushMsgUserRDD)
  }

  /**
    * 去掉已经推荐过的用户数据
    */
  def removeHistoryUser(toDayUserRDD: RDD[(String, (String, Double, String))], historyFileRDD: RDD[String]): RDD[(String, (String, Double, String))] = {
    //如果history不存在或者history为空则直接返回toDayUserRDD
    if (null == historyFileRDD || historyFileRDD.isEmpty()) {
      return toDayUserRDD
    }
    val historyRDD = historyFileRDD.map(rec => {
      val line = rec.split(",")
      (line(0), "")
    })
    val todayClearUserRDD = toDayUserRDD.leftOuterJoin(historyRDD).filter(rec => rec._2._2.isEmpty)
    todayClearUserRDD.map(rec => {
      (rec._1, (rec._2._1._1, rec._2._1._2, rec._2._1._3))
    })
  }

  /**
    * 发送需要发短信的用户
    */
  def msgUserSend(hour: String, toDayFormat: String, dpiConfig: DpiConfig, userRecomBus: UserRecomBusiness, endUserRDD: RDD[((String, (String, Double, String)), Long)], savePath: String, provinceNick: String): RDD[(String, String)] = {
    val max = UserRecommenderUtil.parseMsgMaxDay(userRecomBus.getMsgRecomUser)
    val min = UserRecommenderUtil.parseMsgMinDay(userRecomBus.getMsgRecomUser)
    // 需要发短信的数据
    val pushUserRDD = endUserRDD.filter(rec => {
      (rec._2 > min && rec._2 <= max)
    })
    pushUserRDD.map(rec => {
      val line = rec._1._1 + "," + rec._1._2._1 + "," + rec._1._2._2 + "," + rec._1._2._3
      line
    }).coalesce(dpiConfig.getRepartitionSize).saveAsTextFile(savePath)
    val rate = hour.toInt * userRecomBus.getKeyHourRate
    if ((max % dpiConfig.getTelcomGainCustomer.getPushUserSize) > 0) {
      telcomGainCustomInter(toDayFormat, dpiConfig, 1 + rate, pushUserRDD, provinceNick, userRecomBus)
    } else {
      telcomGainCustomInter(toDayFormat, dpiConfig, 0 + rate, pushUserRDD, provinceNick, userRecomBus)
    }
  }


  /**
    * 发送需要打电话的用户
    */
  def telUserSend(hour: String, toDayFormat: String, dpiConfig: DpiConfig, userRecomBus: UserRecomBusiness, endUserRDD: RDD[((String, (String, Double, String)), Long)], savePath: String, provinceNick: String): RDD[(String, String)] = {
    //获取到需要打电话的用户
    val size = userRecomBus.getTelRecomUser.toInt
    val pushUserRDD = endUserRDD.filter(rec => rec._2 < size);
    pushUserRDD.map(rec => {
      rec._1._1 + "," + rec._1._2._1 + "," + rec._1._2._2 + "," + rec._1._2._3
    }).coalesce(dpiConfig.getRepartitionSize).saveAsTextFile(savePath)
    //如果一天多次执行的话,需要对key进行增加
    val rate = hour.toInt * userRecomBus.getKeyHourRate
    telcomGainCustomInter(toDayFormat, dpiConfig, 0 + rate, pushUserRDD, provinceNick, userRecomBus)
  }

  /**
    * 发送用户信息到获客平台
    *
    * @param endUserRDD
    */
  def telcomGainCustomInter(toDayFormat: String, dpiConfig: DpiConfig, base: Long, endUserRDD: RDD[((String, (String, Double, String)), Long)], provinceNick: String, userRecomBus: UserRecomBusiness): RDD[(String, String)] = {
    LogUtil.printLog(this.getClass.getSimpleName,"[IFB]\t开始进行tel  运营商数据推送-----")
    val key = "IFB_JR_" + toDayFormat + "_" + provinceNick + "_" + userRecomBus.getBusinessTag + "_"
    //因为可能每个列表分配的不是正好的个e数，就会造成短信、电话可能会存在同一个 key ,所以当它们不一样的时候,添加个bas数
    val pushUserRDD = endUserRDD.map(rec => {
      ((rec._2 / dpiConfig.getTelcomGainCustomer.getPushUserSize).toInt + 1 + base, rec._1._1)
    }).reduceByKey((a, b) => a + "^" + b).map(rec => {
      (key + rec._1, rec._2)
    })
    pushUserRDD
  }
}