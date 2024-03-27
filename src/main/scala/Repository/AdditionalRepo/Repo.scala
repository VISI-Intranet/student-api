package Repository.AdditionalRepo

import org.mongodb.scala.Document
import scala.concurrent.{ExecutionContext, Future}
import Connection._

object Repo {

  implicit val ec:  ExecutionContext = scala.concurrent.ExecutionContext.global

  def getNameOfStudentById(studentId: Int): Future[Option[String]] = {
    val studentDocument = Document("studentId" -> studentId)

    MongoDBConnection.studentCollection.find(studentDocument).headOption().map {
      case Some(doc) => Some(doc.getString("name"))
      case None => None
    }
  }

}
