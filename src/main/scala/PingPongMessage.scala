import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._

object PingPongMessage {
  sealed trait PingPongCommand
  case class Start() extends PingPongCommand
  case class Stop() extends PingPongCommand
  case class Ping() extends PingPongCommand
  case class Pong() extends PingPongCommand

  implicit val postfix = scala.language.postfixOps
  implicit val system = ActorSystem("Sys")
  implicit val timeout = Timeout(1 second)
  implicit val exContext = system.dispatcher
}
