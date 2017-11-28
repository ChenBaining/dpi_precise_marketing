package IFB.utils
import java.text.SimpleDateFormat
import java.util.Calendar


/**
  * Created by Administrator on 2017/8/31.
  */
@Deprecated
class ScoreComputionUtil {
  /**
    * 获取给定日期的前n天的日期
    * @param nowDate：给定日期
    * @param interval：间隔，1代表昨天，-1代表明天
    * @return
    */
  def getDateBefore(nowDate: String, interval: Int): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    val dt = dateFormat.parse(nowDate)
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(dt)
    cal.add(Calendar.DATE, - interval)
    val dateBefore = dateFormat.format(cal.getTime)
    dateBefore
  }
}
