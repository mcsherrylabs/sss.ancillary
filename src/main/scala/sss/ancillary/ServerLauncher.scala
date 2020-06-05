package sss.ancillary

import java.io.File
import java.net.InetSocketAddress
import java.util.Collections

import javax.servlet.Servlet
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.{Connector, Handler, HttpConfiguration, HttpConnectionFactory, SecureRequestCustomizer, Server, ServerConnector}
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
import org.eclipse.jetty.http.HttpScheme
import org.eclipse.jetty.security.{ConstraintMapping, ConstraintSecurityHandler}
import org.eclipse.jetty.util.security.Constraint


/**
 * Created by alan on 4/20/16.
 */
case class InitServlet(servletCls: Servlet, path: String)

case class ServletContext(contextPath: String,
                          resourceBase: String, servlets: InitServlet*) {
  def toHandler: ServletContextHandler = ServerLauncher.contextToHandler(this)

}

trait ServerConfig {
  val idleTimeoutMs: Int
  val contextPath: String
  val resourceBase: String
  val httpPort: Int
  val httpsPort: Int
  val useHttpConnector: Boolean
  val useSslConnector: Boolean
  val clientMustAuthenticate: Boolean
  val keyStoreLocation: String
  val keyStorePass: String
  val trustStoreLocation: String
  val trustStorePass: String
  val gracefulShutdownMs: Int
  val hostAddressOpt: Option[String]
}

case class DefaultServerConfig(idleTimeoutMs: Int = 500000,
                               contextPath: String = "/",
                               resourceBase: String = "./",
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


  def singleContext(servletClasses: InitServlet*)(implicit serverConfig: ServerConfig): Server = apply(ServletContext(serverConfig.contextPath,
    serverConfig.resourceBase, servletClasses: _*).toHandler)

  def apply(port: Int, servletClasses: InitServlet*): Server = singleContext(servletClasses: _*)(DefaultServerConfig().copy(httpPort = port))

  /*def addSslConnector(implicit server: Server, serverConfig: ServerConfig): Unit = {
    import serverConfig._
    (useHttpConnector, useSslConnector) match {
      case (true, true) => server.addConnector(createSslConnector(server))
      case (false, true) => server.setConnectors(Array(createSslConnector(server)))
      case (true, false) =>
      case (false, false) => throw new IllegalArgumentException("Use of both ssl and http connectors are false, at least one must be used")
    }

  }*/

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

    //TODO deploy this fix and retest https://www.immuniweb.com/ssl/?id=3TQ22qLh
    sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5", "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", // Disable cipher suites with Diffie-Hellman key exchange to prevent Logjam attack
      //and avoid the ssl_error_weak_server_ephemeral_dh_key error in recent browsers
      "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA")

    sslContextFactory.addExcludeCipherSuites("^.*_(MD5|SHA|SHA1)$")
    // Exclude ciphers that don't support forward secrecy
    sslContextFactory.addExcludeCipherSuites("^TLS_RSA_.*$")
    // Setting required for preventing Poodle attack,
    // see http://stackoverflow.com/questions/26382540/how-to-disable-the-sslv3-protocol-in-jetty-to-prevent-poodle-attack/26388531#26388531
    sslContextFactory.setExcludeProtocols("SSLv3")

    import org.eclipse.jetty.http.HttpVersion
    import org.eclipse.jetty.server.{HttpConfiguration, HttpConnectionFactory, SecureRequestCustomizer, ServerConnector, SslConnectionFactory}
    val http_config = new HttpConfiguration

    http_config.setSecureScheme(HttpScheme.HTTPS.asString())
    http_config.setSecurePort(httpsPort) // 8443)
    http_config.setOutputBufferSize(32768)
    val reqCustomizer = new SecureRequestCustomizer
    reqCustomizer.setStsMaxAge(10886400)
    reqCustomizer.setStsIncludeSubDomains(true)
    http_config.addCustomizer(reqCustomizer)
    //val https_config = new HttpConfiguration(http_config)
    val connFactory = new HttpConnectionFactory(http_config)
    val https = new ServerConnector(server,
      new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString),
      connFactory)

    https.setPort(httpsPort)
    https.setIdleTimeout(idleTimeoutMs)

    // create a https connector
    https
  }

  def apply(hanlders: Handler*)(implicit serverConfig: ServerConfig): Server = {

    val server: Server = new Server(makeInetAddress)


    if (hanlders.nonEmpty) {
      lazy val contextCollection = {
        val col = new ContextHandlerCollection

        val hs = hanlders.toArray.map { h =>
          if (!serverConfig.useHttpConnector) {
            val redirectHandler = buildConstraintSecurityHandler
            redirectHandler.setHandler(h)
            redirectHandler
          } else h
        }

        col.setHandlers(hs)
        col
      }


      server.setHandler(contextCollection)
      hanlders.collect { case s: ServletContextHandler => s } foreach (WebSocketServerContainerInitializer.configureContext)
    }

    server.setStopTimeout(serverConfig.gracefulShutdownMs)
    server.setStopAtShutdown(true)

    (serverConfig.useHttpConnector, serverConfig.useSslConnector) match {
      case (false, true) =>
        server.setConnectors(Array(createHttpConnector(server), createSslConnector (server)))
      case (true, true) =>
        server.addConnector(createSslConnector(server))
      case (true, false) =>
      case (false, false) => throw new IllegalArgumentException("Use of both ssl and http connectors are false, at least one must be used")
    }


    server
  }


  def createHttpConnector(server: Server)(implicit serverConfig: ServerConfig) = {
    val http_config = new HttpConfiguration();
    http_config.setSecureScheme(HttpScheme.HTTPS.asString());
    http_config.setSecurePort(serverConfig.httpsPort)
    http_config.setOutputBufferSize(32768);
    // HTTP connector
    // The first server connector we create is the one for http, passing in the http configuration we configured
    // above so it can get things like the output buffer size, etc. We also set the port (8080) and configure an
    // idle timeout.
    val http = new ServerConnector(server, new HttpConnectionFactory(http_config));
    http.setPort(serverConfig.httpPort)
    http.setIdleTimeout(serverConfig.idleTimeoutMs);
    http
  }

  def makeInetAddress(implicit serverConfig: ServerConfig): InetSocketAddress = {
    serverConfig.hostAddressOpt match {
      case Some(host) => InetSocketAddress.createUnresolved(host, serverConfig.httpPort)
      case None => new InetSocketAddress(serverConfig.httpPort)
    }
  }

  def addServlet(servletDetails: InitServlet)(implicit server: Server): Unit = {
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
    server.getHandlers.collect { case s: ServletContextHandler => s }.find(holder => holder.getServletHandler.getServletContext.getContextPath == contextPath) match {
      case None => throw new IllegalArgumentException(s"No context exists for $contextPath")
      case Some(context) => context.addServlet(new ServletHolder(servletDetails.servletCls), servletDetails.path)
    }
  }

  private def buildConstraintSecurityHandler: ConstraintSecurityHandler = {
    // this configures jetty to require HTTPS for all requests
    val constraint = new Constraint
    constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL)
    val mapping = new ConstraintMapping
    mapping.setPathSpec("/*")
    mapping.setConstraint(constraint)
    val security = new ConstraintSecurityHandler
    security.setConstraintMappings(Collections.singletonList(mapping))
    security
  }
}
