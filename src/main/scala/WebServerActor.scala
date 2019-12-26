import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask

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
  def parseStr(resultStr: String) = {
    var result = resultStr.split(",")
    result = result(1).split(":")
    val str = result(1).substring(1, result(1).length - 2)
    str
  }
  def receive:Receive = {
    case WebCommands.Start => {
      println(self.toString())
      def route:Route = path("joke") {
        get {
          onComplete((webClient ? RequestJoke).mapTo[String]) {
            case Success(result) =>
              complete(JokeJson(parseStr(result)))
            case Failure(exception) =>
              complete(exception.printStackTrace().toString)
          }
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
