package ping_pong

import akka.actor.Props
import ping_pong.PingPongMessage._

object PingPong extends App{
  val pong = system.actorOf(Props(new PongActor),"pong")
  val ping = system.actorOf(Props(new PingActor(10)),"ping")
  ping ! Start
  system.terminate()
}
