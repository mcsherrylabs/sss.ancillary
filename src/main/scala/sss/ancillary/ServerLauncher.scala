package sss.ancillary

import java.io.File
import java.net.InetSocketAddress

import javax.servlet.Servlet
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.{Connector, Handler, Server}
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer




/**
  * Created by alan on 4/20/16.
  */
case class InitServlet(servletCls: Servlet, path: String)
case class ServletContext(contextPath: String ,
                          resourceBase: String, servlets: InitServlet*) {
  def toHandler: ServletContextHandler = ServerLauncher.contextToHandler(this)

}

trait ServerConfig {
  val contextPath: String
  val resourceBase: String
  val httpPort: Int
  val httpsPort: Int
  val useHttpConnector: Boolean
  val useSslConnector: Boolean
  val clientMustAuthenticate : Boolean
  val keyStoreLocation : String
  val keyStorePass :String
  val trustStoreLocation : String
  val trustStorePass :String
  val gracefulShutdownMs: Int
  val hostAddressOpt: Option[String]
}

case class DefaultServerConfig(contextPath: String = "/",
 resourceBase: String =  "./",
 httpPort: Int = 8080,
 httpsPort: Int = 8443,
 useHttpConnector: Boolean = true,
 useSslConnector: Boolean = false,
 clientMustAuthenticate: Boolean = false, // client certs NOT tested as of May 2016,
 keyStoreLocation: String = "",
 keyStorePass: String = "",
 trustStoreLocation: String = "",
 trustStorePass: String = "",
 gracefulShutdownMs: Int = 6000,
 hostAddressOpt: Option[String] = None) extends ServerConfig


object ServerLauncher {

  def contextToHandler(servletContext: ServletContext): ServletContextHandler = {

    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)

    context.setContextPath(servletContext.contextPath)
    context.setResourceBase(servletContext.resourceBase)

    servletContext.servlets foreach { servlet =>
      context.addServlet(new ServletHolder(servlet.servletCls), servlet.path)
    }
    context
  }


  def singleContext(servletClasses : InitServlet*)(implicit serverConfig: ServerConfig) : Server = apply(ServletContext(serverConfig.contextPath,
    serverConfig.resourceBase, servletClasses: _*).toHandler)

  def apply(port: Int, servletClasses : InitServlet*) : Server = singleContext(servletClasses: _*)(DefaultServerConfig().copy(httpPort = port))

  def addSslConnector(implicit server: Server, serverConfig: ServerConfig): Unit = {
    import serverConfig._
    (useHttpConnector, useSslConnector) match {
      case (true, true) => server.addConnector(createSslConnector(server))
      case (false, true) => server.setConnectors(Array(createSslConnector(server)))
      case (true, false) =>
      case (false, false) => throw new IllegalArgumentException("Use of both ssl and http connectors are false, at least one must be used")
    }

  }

  def createSslConnector(server: Server)(implicit serverConfig: ServerConfig): Connector = {
    import serverConfig._

    require(new File(keyStoreLocation).isFile, s"Key store location ($keyStoreLocation) must exist and be a file.")
    val sslContextFactory = new SslContextFactory.Server() //new SslContextFactory(keyStoreLocation)

    sslContextFactory.setKeyStorePath(keyStoreLocation)

    require(Option(keyStorePass).isDefined && keyStorePass.length > 0, "The password may not be empty or null.")
    sslContextFactory.setKeyStorePassword(keyStorePass)
    require(new File(trustStoreLocation).isFile, s"Trust store location ($trustStoreLocation) must exist and be a file.")

    sslContextFactory.setTrustStorePath(trustStoreLocation)

    require(Option(trustStorePass).isDefined && trustStorePass.length > 0, "The password may not be empty or null.")
    sslContextFactory.setTrustStorePassword(trustStorePass)
    sslContextFactory.setNeedClientAuth(clientMustAuthenticate)

    import org.eclipse.jetty.http.HttpVersion
    import org.eclipse.jetty.server.{HttpConfiguration, HttpConnectionFactory, SecureRequestCustomizer, ServerConnector, SslConnectionFactory}
    val http_config = new HttpConfiguration
    http_config.setSecureScheme("https")
    http_config.setSecurePort(8443)
    http_config.setOutputBufferSize(32768)
    val https_config = new HttpConfiguration(http_config)
    val src = new SecureRequestCustomizer
    src.setStsMaxAge(2000)
    src.setStsIncludeSubDomains(true)
    val https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString), new HttpConnectionFactory(https_config))
    https.setPort(httpsPort)
    https.setIdleTimeout(500000)
    // create a https connector
    https
  }

  def apply(hanlders : Handler*)(implicit serverConfig: ServerConfig): Server =  {

    val server: Server = new Server(makeInetAddress)

    if(hanlders.nonEmpty) {
      lazy val contextCollection = {
        val col = new ContextHandlerCollection
        col.setHandlers(hanlders.toArray)
        col
      }

      server.setHandler(contextCollection)
      hanlders.collect{case s: ServletContextHandler => s} foreach (WebSocketServerContainerInitializer.configureContext)
    }

    server.setStopTimeout(serverConfig.gracefulShutdownMs)
    server.setStopAtShutdown(true)


    (serverConfig.useHttpConnector, serverConfig.useSslConnector) match {
      case (true, true) => server.addConnector(createSslConnector(server))
      case (false, true) => server.setConnectors(Array(createSslConnector(server)))
      case (true, false) =>
      case (false, false) => throw new IllegalArgumentException("Use of both ssl and http connectors are false, at least one must be used")
    }


    server
  }


  def makeInetAddress(implicit serverConfig: ServerConfig): InetSocketAddress = {
    serverConfig.hostAddressOpt match {
      case Some(host) => InetSocketAddress.createUnresolved(host, serverConfig.httpPort)
      case None => new InetSocketAddress(serverConfig.httpPort)
    }
  }

  def addServlet(servletDetails: InitServlet)(implicit server: Server):Unit = {
    server.getHandlers.headOption.getOrElse(throw new IllegalArgumentException(s"No contexts at all")) match {
      case s: ServletContextHandler => s.addServlet(new ServletHolder(servletDetails.servletCls), servletDetails.path)
      case s: ContextHandlerCollection => {
        s.getHandlers.headOption.getOrElse(throw new IllegalArgumentException(s"No sub handler at all")) match {
          case s: ServletContextHandler => s.addServlet(new ServletHolder(servletDetails.servletCls), servletDetails.path)
          case s => throw new IllegalArgumentException(s"Cannot add $servletDetails , $s is not a ServletContextHandler")
        }
      }
      case s => throw new IllegalArgumentException(s"Cannot add $servletDetails , $s is not a ServletContextHandler")
    }
  }


  def addServlet(servletDetails: InitServlet, contextPath: String)(implicit server: Server) = {
    server.getHandlers.collect{ case s: ServletContextHandler => s}.find(holder => holder.getServletHandler.getServletContext.getContextPath == contextPath) match {
      case None => throw new IllegalArgumentException(s"No context exists for $contextPath")
      case Some(context) => context.addServlet(new ServletHolder(servletDetails.servletCls), servletDetails.path)
    }
  }

}
