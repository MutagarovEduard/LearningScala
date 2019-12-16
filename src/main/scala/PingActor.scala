import PingPongMessage._
import akka.actor.{Actor, ActorRef}

import scala.util.{Failure, Success}

class PingActor(maxCount:Int) extends Actor {


  var pongRef1:ActorRef = ActorRef.noSender;
  var count:Int = 0
  def countPlus: Unit = {
    count+=1
  }

  def receive:Actor.Receive = {
    case Start =>
      system.actorSelection("user/pong/").resolveOne().onComplete {
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