
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object PingPong extends App{
  sealed trait PingPongCommand
  case class Start() extends PingPongCommand
  case class Stop() extends PingPongCommand
  case class Ping() extends PingPongCommand
  case class Pong() extends PingPongCommand


  implicit val postfix = scala.language.postfixOps
  implicit val system = ActorSystem("Sys")
  implicit val timeout = Timeout(1 second)
  implicit val exContext = system.dispatcher


  class PingActor(maxCount:Int) extends Actor {
    var pongRef1:ActorRef = ActorRef.noSender;
    var count:Int = 0
    def countPlus: Unit = {
      count+=1
    }

    def receive:Actor.Receive = {
      case Start =>
        sys.actorSelection("user/pong/").resolveOne().onComplete {
          case Success(actorRef) =>
            this.pongRef1 = actorRef
            countPlus
            print("ping ")
            pongRef1 ! Pong
          case Failure(exception) => println(exception)
        }

      case Ping =>
        if (count<maxCount) {
          countPlus
          print("ping ")
          pongRef1 ! Pong
        } else {
          pongRef1 ! Stop
          println("Ping stopped")
          context.stop(self)
        }
    }
  }
  class PongActor extends Actor {
    def receive:Actor.Receive = {
      case Pong =>
        println("pong")
        sender() ! Ping
      case Stop =>
        println("Pong stopped")
        context.stop(self)
    }
  }
  val sys = ActorSystem("system")
  val pong = sys.actorOf(Props(new PongActor),"pong")
  val ping = sys.actorOf(Props(new PingActor(10)),"ping")
  ping ! Start
  sys.terminate()
}

