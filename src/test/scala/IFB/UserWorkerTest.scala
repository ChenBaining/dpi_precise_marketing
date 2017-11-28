package IFB

import IFB.domain.{DpiConfig, Province, UserRecomBusiness}
import IFB.utils.{ConfigUtils, TelcomToken, UserRecommenderUtil}
import com.google.gson.Gson
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._

/**
  * Created by CainGao on 2017/9/13.
  */
object UserWorkerTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("UserRecommender").setMaster("local")
    val sc = new SparkContext(conf)
    val xdd = sc.textFile("D:/dataCount.txt")
    val out = xdd.map(rec => {
      rec.split("\\|")
    })
    // Array(id, url, host, Weights, tag, typeStr, provinceNick)
    val tagCounts = out.map(rec => {
      ((rec(0), rec(6), rec(4), rec(2)), rec(3).toDouble)
    }).distinct().map(rec => {
      ((rec._1._1, rec._1._2, rec._1._3), rec._2)
    }).reduceByKey((a: Double, b: Double) => a + b).map(rec => {
      ((rec._1._1, rec._1._2), (rec._2, rec._1._3))
    })
    tagCounts.foreach(println)

    val result = getQuantity(tagCounts)
    val quantity = result.map(line => (line._1._1, (line._1._2, line._2)))
    quantity.map(rec => {
      rec._1 + "\t" + rec._2._1 + "\t" + rec._2._2
    }).foreach(println)
    //      .map(rec => {
    //      ((rec._1._1, rec._1._2, rec._1._3), rec._2)
    //    }).reduceByKey((a: Double, b: Double) => a + b).foreach(println)
  }

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

}



