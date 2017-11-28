package IFB

import IFB.utils.{ConfigUtils, DateUtil, UrlUtil}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by miluo on 2017/8/30. 
  */
object DataFilterTest {

  //      ConfigUtils.setUrlConfigPath(args(1))

  def main(args: Array[String]): Unit = {

    //获取args中的配置路径,读取json配置文件,转换为Entrance对象
    //    if (args.length > 1) {
    //      ConfigUtils.setConfigPath(args(0))
    //    } else {
    //      println("没有传配置文件的路径过来或配置文件的数量不够！")
    //      System.exit(1)
    //    }

    //    val tiemList: java.util.List[String] = DateUtil.hourConverByInterval(1, 5)
    //
    //    val demoStr = "MBLDPI4G.2017110714_shengchan2502.1509212038503.lzo_deflate"
    //    val demoStr2 = "/day_id=20171029/net_type=4g/*2017102902*"
    //    var i = 0


    //    while (i < tiemList.size()) {
    //      //      println(tiemList.get(i))
    //      val suff = tiemList.get(i).split(",")
    //      var result = "/" + suff(0) + "/*"
    //      result += suff(0)
    //      result += suff(1)
    //      result += "*"
    //      println(result)
    //      i += 1;
    //    }


    //    while (i < tiemList.size()) {
    //      val suff = tiemList.get(i).split(",")
    //      var result = "/day_id=" + suff(0) + "/"
    //      result += "net_type=4g"
    //      result += "/*" + suff(0)
    //      result += suff(1)
    //      result += "*"
    //      println(result)
    //      i += 1;
    //    }


    //初始化spark
    //    System.setProperty("HADOOP_USER_NAME", "soft")
    //    System.setProperty("hadoop.home.dir", "D:\\Program Files\\hadoop-common-2.2.0-bin-master\\")
    //    val conf = new SparkConf().setAppName("urlFilter").setMaster("local")
    //    val sc = new SparkContext(conf)
    //
    //    val lines = sc.textFile("C:/Users/Administrator/Desktop/spark样本数据.txt")
    //    val mapresultss: RDD[Array[String]] = lines.map(line => {
    //      line.split("\\|", -1)
    //    })
    //    val rdd9 = sc.makeRDD(Array(("A", 0), ("A", 2), ("B", 1), ("B", 2), ("C", 1)))
    //
    //    val de: RDD[(String, Int)] = rdd9.reduceByKey((x, y) => x + y)
    //
    //
    //    var rdd1 = sc.makeRDD(Array(("A", "1"), ("B", "2"), ("C", "3")), 2)
    //    var rdd2 = sc.makeRDD(Array(("A", "a"), ("C", "c"), ("D", "d")), 2)

    // 引入过滤类g
    //    val dataFilter = new DataFilter()
    //    val rdd = dataFilter.filterDataEntrance(sc, ConfigUtils.getUrlConfig(), ConfigUtils.getDpiConfig());
    //    rdd.foreach(x => println(x(0) + "\t" + x(1) + "\t" + x(2) + "\t" + x(3) + "\t" + x(4) + "\t" + x(5) + "\t" + x(6)))
  }
}
