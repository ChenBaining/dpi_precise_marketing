package IFB.utils

import java.net.URLEncoder
import java.util

import IFB.domain.TelcomGainCustomer
import com.google.gson.{Gson, JsonParser}
import org.apache.commons.codec.digest.DigestUtils
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.log4j.Logger

/**
  * 获取token
  * Created by CainGao on 2017/9/8.
  */
class TelcomToken extends Serializable {

  /**
    * 根据tokenId推送数据
    *
    * @param telcomGainCustomer
    * @param token
    * @param key
    * @param value
    */
  def sendRecomUser(telcomGainCustomer: TelcomGainCustomer, token: String, key: String, value: String): String = {
    Thread.sleep(telcomGainCustomer.getApiRequestDelay)
    println(System.currentTimeMillis()+"\t\t推送数据到获客平台\t\tkey:"+key )
    val nvps = new util.ArrayList[NameValuePair]
    val gainCustomerGetUrl = telcomGainCustomer.getGainUserSendUrl + telcomGainCustomer.getApiKey + "/" + token + ".json?key=" + URLEncoder.encode(key,"UTF-8") + "&value=" + URLEncoder.encode(value,"UTF-8");
    var result = ""
    try {
      result = HTTPUtils.get(gainCustomerGetUrl)
    } catch {
      case e: Exception => println("[IFB] 推送获客平台失败",e)
    }
//    HdfsUtil.createFile("/user/vendorjzsj/data/output/userStoragePath/result_"+key,result.getBytes("utf-8"))
    println(result)
    result
  }

  def parserSuccess(result: String): String = {
    val obj = new JsonParser().parse(result).getAsJsonObject
    var data = ""
    try
      data = obj.get("code").getAsString
    catch {
      case e: Exception =>
        println("[IFB] result获取失败", e)
        throw e
    }
    data
  }


  def getToken(telcomGainCustomer: TelcomGainCustomer): String = {
    println("[IFB]开始推送数据接口数据--------------------")
    val tokenId = makeToken(telcomGainCustomer)
    println("[IFB]本次获取到的tokenId为:" + tokenId)
    return tokenId;
  }

  private def makeToken(telcomGainCustomer: TelcomGainCustomer): String = {
    var tokenId = ""
    try { //setp1 : 获取authCode
      val authKeyUrl = "http://" + telcomGainCustomer.getHost + ":" + telcomGainCustomer.getPort + "/restful/system/publicKey.json?apiKey=" + telcomGainCustomer.getApiKey
      val authkeyJson = HTTPUtils.get(authKeyUrl)
      val authCode = parseJson(authkeyJson)
      //setp2. 获取sign
      val sign = md5(telcomGainCustomer.getApiKey + telcomGainCustomer.getSecretkey + authCode)
      //step3. 获取token
      val tokenUrl = "http://" + telcomGainCustomer.getHost + ":" + telcomGainCustomer.getPort + "/restful/system/token.json?apiKey=" + telcomGainCustomer.getApiKey + "&authCode=" + authCode + "&sign=" + sign
      val content = HTTPUtils.get(tokenUrl)
      tokenId = parseToken(content)
    } catch {
      case e: Exception => println("[IFB] token 获取失败;",e)
    }
    tokenId
  }

  @throws[Exception]
  private def parseToken(tokenJson: String): String = {
    val obj = new JsonParser().parse(tokenJson).getAsJsonObject
    var data = ""
    try
      data = obj.get("data").getAsJsonObject.get("token").getAsString
    catch {
      case e: Exception =>
        println("[IFB] token获取错误{}", e)
        throw e
    }
    data
  }

  def md5(str: String): String = DigestUtils.md5Hex(str)

  @throws[Exception]
  private def parseJson(json: String): String = {
    val obj = new JsonParser().parse(json).getAsJsonObject
    var data = ""
    try
      data = obj.get("data").getAsString
    catch {
      case e: Exception =>
        println("[IFB] authCode获取错误{}", e)
        throw e
    }
    data
  }
}
