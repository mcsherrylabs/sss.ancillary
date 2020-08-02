package sss.ancillary

import us.monoid.web.Resty

object MainClient {

  def main(args: Array[String]): Unit = {
    Resty.ignoreAllCerts()

    val defaultHttpConfig = DynConfig[ServerConfig]("httpServerConfig")


    val b = new Resty().text(s"http://127.0.0.1:${defaultHttpConfig.httpPort}/testhttp/ping")
    println(s"${b.location}")
    val a = new Resty().text(s"https://127.0.0.1:${defaultHttpConfig.httpsPort}/testhttp/ping").toString

    println("asdasd")
  }
}
