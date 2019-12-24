import akka.actor.{ActorRef, Props}

object WebServiceMain {
  import WebCommands._
  def main(args: Array[String]) {
    val webServer:ActorRef = system.actorOf(Props[WebServerActor],"WebServer")
    webServer ! WebCommands.Start
  }
}



