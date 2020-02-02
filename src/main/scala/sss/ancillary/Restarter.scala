package sss.ancillary

import java.io.{File, IOException}
import java.lang.management.ManagementFactory

import scala.collection.JavaConverters._

object Restarter {
  type AttemptRestart = () => Unit
}

class Restarter(val originalArgs: List[String]) {


  private val recoveryFlag: String = "-recover"
  private val tryRecoveryFlag: String = "-tryrecover"
  private val isTryRecover: Boolean = originalArgs.contains(tryRecoveryFlag)
  val recoveryInProgress: Boolean = originalArgs.contains(recoveryFlag)


  def attemptRestart(): Unit = {
    if (isTryRecover) {
      if (!recoveryInProgress) {
        restartWithExtraArgs(recoveryFlag)
      }
    }
    System.exit(-1)
  }


  override def toString: String = {
    val cmd = makeCommand(originalArgs).mkString(" ")
    s"Restarter: $cmd"
  }

  def restartWithExtraArgs(args: String*): Unit = {
    restart(originalArgs ++ args)
  }

  def restartWithoutArgs(args: String*): Unit  = {
    restart(originalArgs.filterNot(args.contains(_)))
  }

  @throws[Exception]
  private def restart(args: List[String]): Unit = {

    val command = makeCommand(args)
    System.out.println(s"Starting '$command'")
    try
      new ProcessBuilder(command.asJava).inheritIO.start
    catch {
      case ex: IOException =>
        ex.printStackTrace()
    }

  }

  private def makeCommand(args: List[String]) = {
    appendVMArgs(javaExecutable()) ++ classPath() ++ entryPoint ++ args
  }

  private def javaExecutable(): List[String] = {
    List(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java")
  }

  private def appendVMArgs(cmd: List[String]): List[String] = {

    val vmArguments: List[String] = ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList

    val javaToolOptionsList: List[String] =
      Option(
        System.getenv("JAVA_TOOL_OPTIONS")
      ).map(s =>
        s.split(" ").toList
      ).getOrElse(List.empty)

    val withoutToolOptions: List[String] = cmd.filterNot(javaToolOptionsList.contains)
    withoutToolOptions ++ vmArguments
  }

  private def classPath(): List[String] = {
    List("-cp", ManagementFactory.getRuntimeMXBean.getClassPath)
  }

  private val entryPoint: List[String] = {
    val stackTrace = new Throwable().getStackTrace
    val stackTraceElement = stackTrace(stackTrace.length - 1)
    val fullyQualifiedClass = stackTraceElement.getClassName
    val entryMethod = stackTraceElement.getMethodName
    if (!(entryMethod == "main")) throw new AssertionError("Entry point is not a 'main()': " + fullyQualifiedClass + '.' + entryMethod)
    List(fullyQualifiedClass)
  }

}
