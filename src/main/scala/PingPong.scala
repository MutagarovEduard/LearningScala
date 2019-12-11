import akka.actor.{Actor, ActorRef, ActorSystem, Props}
object PingPong extends App{
  sealed trait PingPongCommand
  case class Ball(var count:Int, maxCount:Int) extends PingPongCommand
  case class Start(var count1:Int, maxCount:Int, replyTo:ActorRef)
  case class Stop()
  class PingPongActor extends Actor {
    def receive:Actor.Receive = {
      case Start(count,maxCount,replyTo) =>
        println("Started")
        replyTo ! Ball(count+1, maxCount)
      case Ball(cnt:Int, max:Int) =>
        if (cnt < max) {
          println(cnt)
          sender() ! Ball(cnt+1, max)
          if (max == cnt+1) {
            context.stop(self)
          }
        } else {
          println(cnt + "/" + max)
          println("Max count message is reached")
          context.stop(self)
        }
      case _ => "WTF?!"
    }
  }
    val sys = ActorSystem("system")
    val ping = sys.actorOf(Props[PingPongActor])
    val pong = sys.actorOf(Props[PingPongActor])
    ping !  Start(0,10,pong)
    sys.terminate()
}
