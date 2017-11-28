package IFB.utils

import IFB.domain._
import IFB.{DataCount, DataFilter, ScoreCompution}
import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator

/**
  * Created by liam on 2016/10/26.
  */
class DpiRegistrator extends KryoRegistrator{
  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[TelcomToken])
    kryo.register(classOf[UrlUtil])
    kryo.register(classOf[DataCount])
    kryo.register(classOf[DataFilter])
    kryo.register(classOf[ScoreCompution])
    kryo.register(classOf[BaseVal])
    kryo.register(classOf[Business])
    kryo.register(classOf[DpiConfig])
    kryo.register(classOf[Position])
    kryo.register(classOf[Province])
    kryo.register(classOf[TelcomGainCustomer])
    kryo.register(classOf[UrlConfig])
    kryo.register(classOf[UrlSingleConfig])
    kryo.register(classOf[UserRecomBusiness])
  }
}
