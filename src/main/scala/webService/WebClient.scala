package webService

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, headers}
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Future
import scala.util.{Failure, Success}

class WebClient extends Actor {
  import WebCommands._
  def receive: Receive = {
    case RequestJoke => {
      val newSender = sender()
      println(newSender)
       val request = HttpRequest(uri = "https://icanhazdadjoke.com/").withHeaders(headers.RawHeader("Accept", "Accept: application/json"))
       val futureRequest: Future[HttpResponse] = Http().singleRequest(request)
       futureRequest.onComplete {
         case Success(result) =>
           println("Sending message!!!")
           Unmarshal(result.entity).to[JokeJson].map(str => newSender ! str)
         case Failure(exception) => println(exception)
       }
    }
  }
}