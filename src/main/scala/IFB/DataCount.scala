package IFB

import IFB.utils.{ConfigUtils, LogUtil}
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD

/**
  * Created by zc on 2017/8/29.
  */
class DataCount extends Serializable {


  /**
    * 根据传过来的RDD数据集，返回统计
    * 频次，多样性，app访问量，不同类型url访问量
    * 的数量
    *
    * @param out1 :RDD[Array[String] ]
    * @return RDD[(String,(String,Map[String:double]))]
    *         返回结果示例: ("13811111111",("BJ",Map("freqCount" -> 5, "A" -> 3, "hostCount" -> 2, "B" -> 2, "appCount" -> 5)))
    */
  def getCountsByDataSource(out1: RDD[Array[String]]): RDD[(String, (String, Map[String, Double]))] = {

    if (out1 == null || out1.count() == 0) {
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB]进入统计阶段，过滤模块结束数据还剩0条.返回null")
      null
    } else {

      //把数组长度不够或为空的去掉，然后权重跟app不是数字类型字符串的改为数字
      val out = out1.map(o => {

        if (o.length != 7) {
          null
        } else if (o.foldLeft(false)((a, b) => (a || (b == null || b.equals(""))))) {
          null
        } else if (o(3).isInstanceOf[Double]) {
          if (!isIntByRegex(o(5))) {
            Array(o(0), o(1), o(2), "1", o(4), "0", o(6))
          } else {
            Array(o(0), o(1), o(2), "1", o(4), o(5), o(6))
          }
        } else if (!isIntByRegex(o(5))) {
          Array(o(0), o(1), o(2), o(3), o(4), "0", o(6))
        } else {
          Array(o(0), o(1), o(2), o(3), o(4), o(5), o(6))
        }
      }).filter(o => (o != null))
      LogUtil.printLog(this.getClass.getSimpleName, "[IFB]过滤模块结束,进入统计阶段.")


      //读取配置文件获得，不同类型的url的个数和具体实现。
      val dpiConfig = ConfigUtils.getDpiConfig
      if (dpiConfig == null) {
        LogUtil.printLog(this.getClass.getSimpleName, "[IFB]统计数据信息时，获取配置文件信息为空！！！")
        System.exit(1)
      }

      //获得业务列表集合
      val tagList = dpiConfig.getBusinessList
      //获得频次，多样性，app访问量的字段名称集合
      val varList = dpiConfig.getBaseValList

      try {
        //统计用户访问次数。映射为（k,v）结构，进而根据k相同，进行统计
        val freqCount = out.map(line => ((line(0), line(6)), (line(3).toDouble, varList.get(0).getVarName)))
          .reduceByKey((a: (Double, String), b: (Double, String)) => {
            (a._1 + b._1, a._2)
          })

        //统计用户访问网站数量。映射为 （id,host,count），去重，映射为（id,1）,再根据k相同进行统计
        val hostCount = out.map(line => {
          ((line(0), line(6)), line(2), line(3).toDouble)
        }).distinct().map(line => (line._1, (line._3, varList.get(1).getVarName)))
          .reduceByKey((a: (Double, String), b: (Double, String)) => {
            (a._1 + b._1, a._2)
          })

        //统计app访问量
        val appCount = out.map(line => ((line(0), line(6)), ((line(5).toInt) * (line(3).toDouble), varList.get(2).getVarName)))
          .reduceByKey((a: (Double, String), b: (Double, String)) => {
            (a._1 + b._1, a._2)
          })

        //先把确定的统计量union一下
        var quantityUnion = freqCount.union(hostCount).union(appCount)
        // Array(id, url, host, Weights, tag, typeStr, provinceNick)
        //统计不同Tag的访问次数
        val tagCounts = out.map(rec => {
          ((rec(0), rec(6), rec(4), rec(2)), rec(3).toDouble)
        }).distinct().map(rec => {
          ((rec._1._1, rec._1._2, rec._1._3), rec._2)
        }).reduceByKey((a: Double, b: Double) => a + b).map(line => {
          ((line._1._1, line._1._2), (line._2, line._1._3))
        })

        quantityUnion = quantityUnion.union(tagCounts)
        //（(phone,privnce),分数，标签）

        //////////////////////////////////////////////////////////////////////

        //循环统计不同Tag的访问次数
        for (x <- 0 to tagList.size - 1) {
          val tagCounts = out.map(line => {
            ((line(0), line(6)), line(4), getValue(tagList.get(x).getBusinessTag, line(4), line(3).toDouble))
          }).map(line => (line._1, (line._3, tagList.get(x).getBusinessTag)))
            .reduceByKey((a: (Double, String), b: (Double, String)) => {
              (a._1 + b._1, a._2)
            })
          //union进quantityUnion中
          quantityUnion = quantityUnion.union(tagCounts)
        }

        //////////////////////////////////////////////////////////////////////

        //结合在一起,现在结果是((String,String),Map[String:double])
        val result = getQuantity(quantityUnion)
        //转换成(String,(String,Map[String:double]))并返回结果
        val quantity = result.map(line => (line._1._1, (line._1._2, line._2)))
        //返回结果
        quantity
      } catch {
        case ex: Exception => {
          LogUtil.printLog(this.getClass.getSimpleName, "[IFB]统计时发生错误！")
          ex.printStackTrace()
          System.exit(1)
          null
        }
      }
    }
  }

  //把union在一起的数据集转换成((String,String),Map[String:double])，并把key相同的放在一起
  def getQuantity(quantityUnion: RDD[((String, String), (Double, String))]): RDD[((String, String), Map[String, Double])] = {

    //aggregateByKey函数的初始值
    val initialCount: Map[String, Double] = Map()

    //聚合在一起以(String,Map(String:Double)) 的方式返回
    val quantity = quantityUnion.aggregateByKey(initialCount)(tupleToMap, distinctMap)

    quantity
  }

  //不同分区传入的map合并在一起
  val distinctMap = (line1: Map[String, Double], line2: Map[String, Double]) => {
    line1 ++ line2
  }

  //把元组转换成map
  val tupleToMap = (line2: Map[String, Double], line1: (Double, String)) => {
    line2 + (line1._2 -> line1._1)
  }

  //循环统计不同Tag的访问次数时，判断是否包含某个tag
  def getValue(str1: String, str2: String, weight: Double): Double = {
    val count: Double = if (str1.equals(str2)) {
      weight
    } else {
      0.0
    }
    count
  }

  //判断一个字符串是否是数字组成
  def isIntByRegex(s: String): Boolean = {
    val pattern = """^(\d+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

}
