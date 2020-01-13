package ping_pong

import akka.actor.Actor
import ping_pong.PingPongMessage.{Pong, Stop, _}

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
