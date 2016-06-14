package sss.ancillary

import org.scalatest.{FlatSpec, Matchers}
import org.scalatra.ScalatraServlet
import us.monoid.web.Resty

/**
  * Created by alan on 5/7/16.
  */

class TestServlet extends ScalatraServlet {

  get("/ping") {
    "pong"
  }
}

class  ServerLauncherSpec extends FlatSpec with Matchers {

  Resty.ignoreAllCerts()
  val port = 9999
  val defaultHttpConfig = DynConfig[ServerConfig]("httpServerConfig")

  "A http only server " must " only serve http requests " in {
    val server = ServerLauncher(port, InitServlet(new TestServlet, "/testhttp/*"))
    server.start
    assert(new Resty().text(s"http://localhost:$port/testhttp/ping").toString === "pong")
    intercept[Exception] {
      new Resty().text(s"https://127.0.0.1:${defaultHttpConfig.httpsPort}/testhttp/ping")
    }
    server.stop
  }

  "A http(s) server " must " serve http and https requests " in {

    val server = ServerLauncher.singleContext(defaultHttpConfig, InitServlet(new TestServlet, "/testhttp/*"))
    server.start
    assert(new Resty().text(s"https://127.0.0.1:${defaultHttpConfig.httpsPort}/testhttp/ping").toString === "pong")
    assert(new Resty().text(s"http://localhost:${defaultHttpConfig.httpPort}/testhttp/ping").toString === "pong")
    server.stop
  }

  "A https-only server " must " only serve https requests " in {

    val config = DefaultServerConfig().copy(
      useHttpConnector = false,
      useSslConnector = true,
      gracefulShutdownMs = 10,
      keyStoreLocation = defaultHttpConfig.keyStoreLocation,
      keyStorePass = defaultHttpConfig.keyStorePass,
      trustStoreLocation = defaultHttpConfig.keyStoreLocation,
      trustStorePass = defaultHttpConfig.trustStorePass)

    val server = ServerLauncher.singleContext(config, InitServlet(new TestServlet, "/testhttp/*"))
    server.start
    assert(new Resty().text(s"https://127.0.0.1:${defaultHttpConfig.httpsPort}/testhttp/ping").toString === "pong")
    intercept[Exception] {
      new Resty().text(s"http://localhost:${defaultHttpConfig.httpPort}/testhttp/ping")
    }
    server.stop
  }

  "A http server " must " allow servlets to be added " in {
    val server = ServerLauncher(port)
    val toBeAdded = InitServlet(new TestServlet, "/testhttp/*")
    server.start

    intercept[Exception] {
      new Resty().text(s"http://localhost:$port/testhttp/ping")
    }

    server.addServlet(toBeAdded)
    assert(new Resty().text(s"http://localhost:$port/testhttp/ping").toString === "pong")

    server.stop
  }

  "A http server " must " allow multiple contexts to be added " in {
    val server = ServerLauncher(defaultHttpConfig,
      ServletContext("", "", InitServlet(new TestServlet, "/testhttp/*")),
      ServletContext("/another", "somewhere", InitServlet(new TestServlet, "/testhttp2/*")))

    server.start

    assert(new Resty().text(s"http://localhost:${defaultHttpConfig.httpPort}/testhttp/ping").toString === "pong")
    assert(new Resty().text(s"http://localhost:${defaultHttpConfig.httpPort}/another/testhttp2/ping").toString === "pong")

    server.stop
  }

}
