package IFB.domain

//import org.apache.hadoop.hbase.HBaseConfiguration
//import org.apache.hadoop.hbase.mapreduce.TableInputFormat
//import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by miluo on 2017/10/16.
  */
object DataFilterHBaseTest {
  def main(args: Array[String]): Unit = {
//
//    //初始化spark
//    System.setProperty("HADOOP_USER_NAME", "soft")
//    System.setProperty("hadoop.home.dir", "D:\\Program Files\\hadoop-common-2.2.0-bin-master\\")
//    val confs = new SparkConf().setAppName("urlFilter").setMaster("local")
//    val sc = new SparkContext(confs)
//    val conf = HBaseConfiguration.create()
//    conf.set(TableInputFormat.INPUT_TABLE, "unicom_dpi")
//    //设置读取的表
//    conf.addResource("hbase-site.xml")
//
//    // 添加资源文件
//    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
//      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
//      classOf[org.apache.hadoop.hbase.client.Result])
//
//    hBaseRDD.foreach({ case (_, result) =>
//      val key = Bytes.toString(result.getRow)
//      val name = Bytes.toString(result.getValue("colnum1".getBytes, "source_port".getBytes))
//      val gender = Bytes.toString(result.getValue("colnum1".getBytes, "user_agent".getBytes))
//      val age = Bytes.toString(result.getValue("colnum1".getBytes, "total_flow".getBytes))
//      println("Row key:" + key + " Name:" + name + " Gender:" + gender + " Age:" + age)
//    })
  }

}
