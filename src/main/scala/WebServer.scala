import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, headers}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}

object WebServer {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val value = scala.language.postfixOps
  implicit val timeout = Timeout(300 millisecond)

  var responseAsString: Future[String] = Future{"Nil"}
    def main(args: Array[String]) {
      def route =
        path("joke") {
          get {
            val request = HttpRequest(uri = "https://icanhazdadjoke.com/").withHeaders(headers.RawHeader("Accept","Accept: application/json"))
            val futureRequest: Future[HttpResponse] = Http().singleRequest(request)
            futureRequest.onComplete {
              case Success(result) =>
                responseAsString = Unmarshal(result.entity).to[String]
              case Failure(exception) => println(exception)
            }
            var result:Array[String] = responseAsString.toString.split(",")
            result = result(1).split(":")
            var str = result(1).substring(1,result(1).length-2)
            complete(str)
          }
        }
      val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
      println(s"Server online at http://localhost:8080/joke\nPress RETURN to stop...")
      StdIn.readLine()
      bindingFuture.flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }


