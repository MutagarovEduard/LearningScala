package mongo_task

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros
import scala.concurrent.duration._

object CommandsForMongodb {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val value = scala.language.postfixOps
  implicit val timeout = Timeout(10 seconds)

  system.actorOf(Props[FileListingActor],"fileListingActor")
  system.actorOf(Props[MongoActor],"mongo")

  case class FileDoc(_id: ObjectId, name: String, dir: String)
  object FileDoc{
    def apply(name: String, dir: String): FileDoc =
      new FileDoc(new ObjectId(), name: String, dir: String)
  }
  implicit val mongoCodec = Macros.createCodecProvider[FileDoc]

  case class FilesDir(dir:String)
  case class Start()
}
