import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}
class WebServerActor extends Actor {
  import WebCommands._

  system.actorOf(Props[WebClient],"WebClient")
  var webClient:ActorRef = ActorRef.noSender

  system.actorSelection("/user/WebClient/").resolveOne().onComplete {
    case Success(ref) => webClient = ref
    case Failure(ex) => println(ex)
  }

  def receive:Receive = {
    case WebCommands.Start => {
      println(self.toString())
      def route:Route = path("joke") {
        get {
          var requestResult = ""
          val req = (webClient ? RequestJoke).mapTo[String]
          val resultStr:String = Await.result(req,4 seconds)
          if (req.isCompleted) {
            var result = resultStr.split(",")
            result = result(1).split(":")
            val str = result(1).substring(1, result(1).length - 2)
            complete(JokeJson(str))
          }
          else complete("Error")
        }
      }
      val bindingFuture = WebCommands.binding(route)
      println(s"Server online at http://localhost:8080/joke\nPress RETURN to stop...")
      StdIn.readLine()
      bindingFuture.flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
    case _ => "WTF"
  }
}
