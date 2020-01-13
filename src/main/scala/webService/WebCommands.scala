package webService

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

object WebCommands extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val value = scala.language.postfixOps
  implicit val timeout = Timeout(10 seconds)

  case class JokeJson(joke:String)
  implicit val jokeForm = jsonFormat1(JokeJson)

  case class RequestJoke()
  case class ResponseJoke(str:String)
  case class Start()


  def binding(routes:Route): Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
}
