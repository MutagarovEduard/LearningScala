import akka.actor.{Actor, ActorRef, ActorSystem, Props}
object PingPong extends App{
  sealed trait PingPongCommand
  case class Start() extends PingPongCommand
  case class Stop() extends PingPongCommand
  case class Ping() extends PingPongCommand
  case class Pong() extends PingPongCommand

  class PingActor(maxCount:Int, pongRef:ActorRef) extends Actor {
    var count:Int = 0
    def countPlus: Unit = {
      count+=1
    }
    def receive:Actor.Receive = {
      case Start =>
        countPlus
        print("ping ")
        pongRef ! Pong
      case Ping =>
        if (count<maxCount) {
          countPlus
          print("ping ")
          pongRef ! Pong
        } else {
          pongRef ! Stop
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
  val pong = sys.actorOf(Props(new PongActor))
  val ping = sys.actorOf(Props(new PingActor(10,pong)))
  ping ! Start
  sys.terminate()
}

