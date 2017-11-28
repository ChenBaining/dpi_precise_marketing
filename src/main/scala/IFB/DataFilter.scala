package IFB

import java.util

import IFB.domain._
import IFB.utils._
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

/**
  * spark数据过滤
  * Created by miluo on 2017/8/30. 
  */
class DataFilter extends Serializable {

  /**
    * 函数出口处
    *
    * @param sc
    * @param urlConfigList
    * @param dpiConfig
    * @return
    */
  def filterDataEntrance(sc: SparkContext, urlConfigList: util.List[UrlConfig], dpiConfig: DpiConfig): RDD[Array[String]] = {
    var totalRdd: RDD[Array[String]] = null

    val broadcastVar = sc.broadcast(urlConfigList)
    val broadUrlConfigList = broadcastVar.value

    LogUtil.printLog(this.getClass.getSimpleName, "[IFB] 加载时间参数")
    LogUtil.printLog(this.getClass.getSimpleName, "[IFB]初始化信息：[省份个数：" + dpiConfig.getProvinceList().size + "]")
    var i, y, z = 0;
    while (i < dpiConfig.getProvinceList().size()) {
      while (y < dpiConfig.getProvinceList().get(i).getPositionList().size()) {
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB]获取省份/市【" + dpiConfig.getProvinceList().get(i).getProvinceName + "】并且是【" + dpiConfig.getProvinceList().get(i).getPositionList().get(y).getNetworkType() + "】的数据")
        val intervalHour = dpiConfig.getProvinceList().get(i).getIntervalHour
        val period = dpiConfig.getProvinceList().get(i).getPeriod
        val timeList: java.util.List[String] = DateUtil.hourConverByInterval(intervalHour, period)
        while (z < timeList.size()) {
          val suBo = timeList.get(z).split(",")
          //dpiUrlPath 原来的url
          var dpiUrlPath = dpiConfig.getProvinceList().get(i).getPositionList().get(y).getDpiPath() + "/"
          dpiUrlPath += suBo(0) + "/*"
          dpiUrlPath += suBo(0)
          dpiUrlPath += suBo(1)
          dpiUrlPath += "*"

          //newDpiUrlPath 新的url
          var newDpiUrlPath = dpiConfig.getProvinceList().get(i).getPositionList().get(y).getNewDpiPath
          newDpiUrlPath += "/day_id=" + suBo(0) + "/"
          newDpiUrlPath += "net_type=" + dpiConfig.getProvinceList().get(i).getPositionList().get(y).getNetworkType.toLowerCase
          newDpiUrlPath += "/*" + suBo(0)
          newDpiUrlPath += suBo(1)
          newDpiUrlPath += "*"

          //初始化选择数据
          val words = conversionData(sc, dpiConfig.getProvinceList().get(i), dpiUrlPath, newDpiUrlPath)
          if (words == null) {
            LogUtil.printLog(this.getClass.getSimpleName, "[IFB]words为空")
          } else {
            //log.info("获取到数据量为：【" + words.count() + "】")
            LogUtil.printLog(this.getClass.getSimpleName, "[IFB]初始化获取数据完成")
            //过滤数据
            val filterDataRDD = filterData(words, broadUrlConfigList, dpiConfig.getProvinceList().get(i).getPositionList().get(y)) //.coalesce(dpiConfig.getCoalesceNumber)
            //log.info("过滤后数据量为：【" + filterDataRDD.count() + "】")
            LogUtil.printLog(this.getClass.getSimpleName, "[IFB]过滤数据完成")
            //组合数据

            var x = 0;
            while (x < broadUrlConfigList.size()) {
              val combinationDataRDD = combinationData(filterDataRDD, broadUrlConfigList.get(x), dpiConfig.getProvinceList().get(i).getPositionList().get(y), dpiConfig.getProvinceList().get(i).getProvinceNick());
              // log.info("组合后数据量为：【" + combinationDataRDD.count() + "】")
              LogUtil.printLog(this.getClass.getSimpleName, "[IFB]组合数据完成")
              if (null == totalRdd)
                totalRdd = combinationDataRDD
              else
                totalRdd = totalRdd.union(combinationDataRDD)

              x += 1
            }
          } //words 是否为空
          z += 1;
        }
        z = 0
        y += 1;
      }
      y = 0;
      i += 1;
    }

    if (totalRdd == null) {
      LogUtil.printLog(this.getClass.getSimpleName, "最终合并数据后总数量为【0】");
    } else {
      totalRdd.persist(StorageLevel.fromString(dpiConfig.getStorageLevel))
      LogUtil.printLog(this.getClass.getSimpleName, "最终合并数据后总数量为【" + totalRdd.count() + "】");
    }
    totalRdd
  }

  /**
    * 转换原始数据，转成可以截取过滤的样子
    *
    * @param sc
    * @param  province 对象
    * @return 返回一个RDD
    */
  def conversionData(sc: SparkContext, province: Province, urlSuffix: String, newUrlSuffix: String): RDD[Array[String]] = {
    //    if (province.getIntervalDay() == 0) {
    //      ConfigUtils.setDateStr(DateUtil.dayConvertByInterval(ConfigUtils.getDpiConfig.getGlobalIntervalDay())) //当前日期减掉配置文件的天数
    //    } else {
    //      ConfigUtils.setDateStr(DateUtil.dayConvertByInterval(province.getIntervalDay())) //当前日期减掉配置文件的天数
    //    }
    //将文件夹和文件名组合 eg-> E:/data/20170831
    //val dpiUrl = position.getDpiPath() + "/" + urlSuffix

    LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的DPI地址为：" + urlSuffix + " 或者是： " + newUrlSuffix)

    val errRdd: RDD[Array[String]] = null
    if (HdfsUtil.pathExists(newUrlSuffix)) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的地址为：" + newUrlSuffix)
      //判断文件类型
      if (ConfigUtils.getDpiConfig().getAccessDataType().equals("byte")) {
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的DPI类型为：" + ConfigUtils.getDpiConfig().getAccessDataType());
        val lines = sc.sequenceFile[org.apache.hadoop.io.LongWritable, org.apache.hadoop.io.Text](newUrlSuffix)
        lines.map(line => {
          line._2.toString.split("\\|", -1)
        })
      } else {
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的DPI类型为：" + ConfigUtils.getDpiConfig().getAccessDataType());
        val lines = sc.textFile(newUrlSuffix)
        lines.map(line => {
          line.split("\\|", -1)
        })
      }
    } else if (HdfsUtil.pathExists(urlSuffix)) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的地址为：" + urlSuffix)
      //判断文件类型
      if (ConfigUtils.getDpiConfig().getAccessDataType().equals("byte")) {
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的DPI类型为：" + ConfigUtils.getDpiConfig().getAccessDataType());

        val lines = sc.sequenceFile[org.apache.hadoop.io.LongWritable, org.apache.hadoop.io.Text](urlSuffix)
        lines.map(line => {
          line._2.toString.split("\\|", -1)
        })
      } else {
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 加载的DPI类型为：" + ConfigUtils.getDpiConfig().getAccessDataType());
        val lines = sc.textFile(urlSuffix)
        lines.map(line => {
          line.split("\\|", -1)
        })
      }
    } else {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB][数据加载] 路径不存在：");
      errRdd
    }
  }

  /**
    * 过滤掉不相关的数据
    */
  def filterData(words: RDD[Array[String]], urlConfigList: java.util.List[UrlConfig], position: Position): RDD[Array[String]] = {
    // 过滤出有效的DPI记录
    val pos = position.getUserAgentPosition
    val ua = ConfigUtils.getDpiConfig().getFilterUserAgent
    val sufiix = ConfigUtils.getDpiConfig().getFilterUrlSuffix()
    words.filter(line => {
      var flag = false
      //过滤后缀名与配置为文件配置相同的数据
      if (line.length > position.getUrlPosition && !UrlUtil.getUrlSuffix(line(position.getUrlPosition()), sufiix)) {
        //判断User-Agent 是否与配置文件中所匹配
        if (line.length > pos && UrlUtil.getUserAgent(line(pos), ua)) {
          //循环第一层，获取批次，例如 A类 一个批次  B类一个批次
          var i, y = 0;
          while (i < urlConfigList.size) {
            while (y < urlConfigList.get(i).getUrlSingleConfig().size()) {
              flag ||= line(position.getUrlPosition()).contains(urlConfigList.get(i).getUrlSingleConfig().get(y).getUrl())
              y += 1;
            }
            y = 0;
            i += 1;
          } //while
        } //user-agent
      } //urlSuffix
      flag
    })
  }

  /**
    * 匹配需要字段，重新生成集合
    */
  def combinationData(filterData: RDD[Array[String]], urlConfig: UrlConfig, position: Position, priven: String): RDD[Array[String]] = {
    filterData.map(line => {
      var id = ""
      var url = ""
      var host = ""
      var Weights = ""
      var tag = ""
      var typeStr = ""
      var provinceNick = ""
      var flag: Boolean = false

      var i, y = 0
      //获取批次内，具体的url信息
      while (y < urlConfig.getUrlSingleConfig().size()) {
        //匹配到url地址
        if (line(position.getUrlPosition).contains(urlConfig.getUrlSingleConfig().get(y).getUrl()) && !flag) {
          //获得权重信息 如果权重信息为null 获取批次的全局权重
          flag = true
          id = line(position.getUserPhonePosition) //用户手机号
          url = line(position.getUrlPosition) //用户访问的URL
          host = UrlUtil.getHost(url) //host地址
          tag = urlConfig.getTag //tag标识
          typeStr = urlConfig.getUrlSingleConfig.get(y).getUrlType + "" //是什么类型数据
          provinceNick = priven
          if (urlConfig.getUrlSingleConfig.get(y).getWeights != null) {
            //使用自身权重
            Weights = urlConfig.getUrlSingleConfig.get(y).getWeights + ""
          } else {
            Weights = urlConfig.getGlobalWeights + ""
          } //if
        } //if
        y += 1;
      }
      y = 0;
      Array(id, url, host, Weights, tag, typeStr, provinceNick)
    })
  }

}
