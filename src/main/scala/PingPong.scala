import PingPongMessage._
import akka.actor.Props

object PingPong extends App{
  val pong = system.actorOf(Props(new PongActor),"pong")
  val ping = system.actorOf(Props(new PingActor(10)),"ping")
  ping ! Start
  system.terminate()
}

