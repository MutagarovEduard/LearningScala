import PingPongMessage._
import akka.actor.{Actor, ActorRef}

import scala.util.{Failure, Success}

class PingActor(maxCount:Int) extends Actor {

  var pongRef:ActorRef = ActorRef.noSender;
  var count:Int = 0
  def countPlus(): Unit = {
    count+=1
  }

  def pingMsg = {
    countPlus()
    print("ping ")
    pongRef ! Pong
  }

  def receive:Actor.Receive = {
    case Start =>
      system.actorSelection("user/pong/").resolveOne().onComplete {
        case Success(actorRef) =>
          pongRef = actorRef
          pingMsg
        case Failure(exception) => println(exception)
      }

    case Ping =>
      if (count<maxCount) {
        pingMsg
      } else {
        pongRef ! Stop
        println("Ping stopped")
        context.stop(self)
      }
  }
}