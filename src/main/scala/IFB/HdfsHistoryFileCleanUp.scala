package IFB

import IFB.domain.DpiConfig
import IFB.utils.HdfsUtil

import scala.collection.JavaConverters._

/**
  * HDFS历史文件清理
  * Created by CainGao on 2017/9/18.
  */
class HdfsHistoryFileCleanUp {

  /**
    * 清理超过时限的文件夹
    * @param dpiConfig
    */
  def clear(dpiConfig: DpiConfig): Unit ={
    val historyDay = cleanTime(dpiConfig)
    //删除用户目录下可以清理的目录
    HdfsUtil.deleteHistoryDir(dpiConfig.getUserStoragePath,historyDay + 1)
    HdfsUtil.deleteHistoryDir(dpiConfig.getScoreStoragePath,dpiConfig.getRangeDay + 1)
  }

  /** 获取需要清理多少天之前的数据
    *
    * @param dpiConfig
    * @return
    */
  def cleanTime(dpiConfig:DpiConfig): Int ={
    var timerange = 0
    //当logic=0时,使用的是不同的分发地址.
    if(dpiConfig.getUserProcessLogic.equals("1")){
      timerange = dpiConfig.getUserBaseRecomBusiness.getRangeDay.replace("-","").toInt
    }else{
      val provinces = dpiConfig.getProvinceList.asScala
      //获取到所有的省份
      for (province <- provinces){
        val busniess = province.getUserRecomBusinessList.asScala
        //获取到该省份下的所有业务数据
        for (userBus  <- busniess){
          val busRange = userBus.getRangeDay.replace("-","").toInt
          //当业务数据大于当前的timerange时,使用业务数据
          if (busRange>timerange){
            timerange = busRange
          }
        }
      }
    }
    timerange
  }
}
