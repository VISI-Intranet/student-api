package repository

import java.util.Date
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import org.mongodb.scala.model.Filters.{equal, regex}
import org.mongodb.scala.model.Updates.{addToSet, combine, set}
import org.mongodb.scala.result.UpdateResult
import scala.util.Try
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Connection._
import Model._

import java.text.SimpleDateFormat

class FaculityRepository(implicit ec: ExecutionContext) {

  def getAllFaculties(): Future[List[Faculity]] = {
    val futureFaculties = MongoDBConnection.faculityCollection.find().toFuture()

    futureFaculties.map { docs =>
      Option(docs).map(_.map { doc =>
        Faculity(
          id = doc.getInteger("id"),
          name = doc.getString("name"),
          listCafedra = Option(doc.getList("listCafedra", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          listStudent = Option(doc.getList("listStudent", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          address = doc.getString("address"),
          achievementsFaculty = Option(doc.getList("achievementsFaculty", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          phoneNumber = doc.getInteger("phoneNumber"),
          graduates = Option(doc.getList("graduates", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          trainingPrograms = Option(doc.getList("trainingPrograms", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          listCouse = Option(doc.getList("listCouse", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getFaculityById(faculityId: Int): Future[Option[Faculity]] = {
    val faculityDocument = Document("id" -> faculityId)

    MongoDBConnection.faculityCollection.find(faculityDocument).headOption().map {
      case Some(doc) =>
        Some(
          Faculity(
            id = doc.getInteger("id"),
            name = doc.getString("name"),
            listCafedra = Option(doc.getList("listCafedra", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            listStudent = Option(doc.getList("listStudent", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            address = doc.getString("address"),
            achievementsFaculty = Option(doc.getList("achievementsFaculty", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            phoneNumber = doc.getInteger("phoneNumber"),
            graduates = Option(doc.getList("graduates", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            trainingPrograms = Option(doc.getList("trainingPrograms", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            listCouse = Option(doc.getList("listCouse", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
          )
        )
      case None => None
    }
  }

  def addFaculity(faculity: Faculity): Future[String] = {
    val faculityDocument = BsonDocument(
      "id" -> BsonInt32(faculity.id),
      "name" -> BsonString(faculity.name),
      "listCafedra" -> BsonArray(faculity.listCafedra.map(BsonString(_))),
      "listStudent" -> BsonArray(faculity.listStudent.map(BsonString(_))),
      "address" -> BsonString(faculity.address),
      "achievementsFaculty" -> BsonArray(faculity.achievementsFaculty.map(BsonString(_))),
      "phoneNumber" -> BsonInt32(faculity.phoneNumber),
      "graduates" -> BsonArray(faculity.graduates.map(BsonString(_))),
      "trainingPrograms" -> BsonArray(faculity.trainingPrograms.map(BsonString(_))),
      "listCouse" -> BsonArray(faculity.listCouse.map(BsonString(_)))
    )

    MongoDBConnection.faculityCollection.insertOne(faculityDocument).toFuture().map(_ => s"Факультет ${faculity.name} был добавлен в базу данных.")
  }

  def deleteFaculity(faculityId: Int): Future[String] = {
    val faculityDocument = Document("id" -> faculityId)
    MongoDBConnection.faculityCollection.deleteOne(faculityDocument).toFuture().map(_ => s"Факультет с id $faculityId был удален из базы данных.")
  }

  def updateFaculity(faculityId: Int, updatedFaculity: Faculity): Future[String] = {
    val filter = Document("id" -> faculityId)

    val faculityDocument = BsonDocument(
      "$set" -> BsonDocument(
        "id" -> BsonInt32(updatedFaculity.id),
        "name" -> BsonString(updatedFaculity.name),
        "listCafedra" -> BsonArray(updatedFaculity.listCafedra.map(BsonString(_))),
        "listStudent" -> BsonArray(updatedFaculity.listStudent.map(BsonString(_))),
        "address" -> BsonString(updatedFaculity.address),
        "achievementsFaculty" -> BsonArray(updatedFaculity.achievementsFaculty.map(BsonString(_))),
        "phoneNumber" -> BsonInt32(updatedFaculity.phoneNumber),
        "graduates" -> BsonArray(updatedFaculity.graduates.map(BsonString(_))),
        "trainingPrograms" -> BsonArray(updatedFaculity.trainingPrograms.map(BsonString(_))),
        "listCouse" -> BsonArray(updatedFaculity.listCouse.map(BsonString(_)))
      )
    )

    MongoDBConnection.faculityCollection.updateOne(filter, faculityDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о факультете с id $faculityId была успешно обновлена."
      } else {
        s"Обновление информации о факультете с id $faculityId не выполнено. Возможно, факультет не найден или произошла ошибка в базе данных."
      }
    }
  }
}
