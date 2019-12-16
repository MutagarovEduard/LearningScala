import PingPongMessage.{Pong, Stop, _}
import akka.actor.Actor

class PongActor extends Actor{

  def receive:Actor.Receive = {
    case Pong =>
      println("pong")
      sender() ! Ping
    case Stop =>
      println("Pong stopped")
      context.stop(self)
  }
}
