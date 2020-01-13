package mongo_task

import java.io.File

import akka.actor.{Actor, ActorRef}
import akka.pattern._
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.util.Success

class MongoActor extends Actor{
  import CommandsForMongodb._

  var ref:ActorRef = ActorRef.noSender
  system.actorSelection("user/fileListingActor").resolveOne.onComplete{
    case Success(value) => ref = value
  }

  var collectionDB:MongoCollection[FileDoc] = _

  override def preStart = {
    val mongoClient = MongoClient()
    val codecRegistries = fromRegistries(fromProviders(mongoCodec),DEFAULT_CODEC_REGISTRY)
    val dataBase: MongoDatabase = mongoClient.getDatabase("mydb").withCodecRegistry(codecRegistries)
    collectionDB = dataBase.getCollection("test")
  }

  def receive: Receive = {
    case Start =>
      var fileList:List[File] = List.empty
      val futureList = (ref ? FilesDir("/Users/admin/Documents/")).mapTo[List[File]]
      futureList.onComplete{
        case Success(value) =>
          fileList = value
          var fileDocList:List[FileDoc] = List()
          for (i <- 0 to fileList.size-1) {
            fileDocList::=FileDoc(fileList(i).getName,fileList(i).getParent)
          }
          collectionDB.insertMany(fileDocList).toFuture.onComplete{
            case Success(_) => println("Success")
          }

      }


  }
}
