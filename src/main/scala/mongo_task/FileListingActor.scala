package mongo_task

import java.io.File

import akka.actor.Actor
import akka.pattern.pipe
import mongo_task.CommandsForMongodb.{FilesDir, _}

import scala.concurrent.Future
class FileListingActor extends Actor {
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def receive: Receive = {
    case FilesDir(dir) =>
      val futureDirList:Future[List[File]] = Future {
        getListOfFiles(dir)
      }
      futureDirList.pipeTo(sender)
    case _ =>
  }
}

