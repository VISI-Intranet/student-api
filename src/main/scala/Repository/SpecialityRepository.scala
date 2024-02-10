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

class SpecialityRepository(implicit ec: ExecutionContext) {

  def getAllSpecialities(): Future[List[Speciality]] = {
    val futureSpecialities = MongoDBConnection.specialityCollection.find().toFuture()

    futureSpecialities.map { docs =>
      Option(docs).map(_.map { doc =>
        Speciality(
          specialityId = doc.getInteger("specialityId"),
          name = doc.getString("name")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getSpecialityById(specialityId: Int): Future[Option[Speciality]] = {
    val specialityDocument = Document("specialityId" -> specialityId)

    MongoDBConnection.specialityCollection.find(specialityDocument).headOption().map {
      case Some(doc) =>
        Some(
          Speciality(
            specialityId = doc.getInteger("specialityId"),
            name = doc.getString("name")
          )
        )
      case None => None
    }
  }

  def addSpeciality(speciality: Speciality): Future[String] = {
    val specialityDocument = BsonDocument(
      "specialityId" -> BsonInt32(speciality.specialityId),
      "name" -> BsonString(speciality.name)
    )

    MongoDBConnection.specialityCollection.insertOne(specialityDocument).toFuture().map(_ => s"Специальность ${speciality.name} была добавлена в базу данных.")
  }

  def deleteSpeciality(specialityId: Int): Future[String] = {
    val specialityDocument = Document("specialityId" -> specialityId)
    MongoDBConnection.specialityCollection.deleteOne(specialityDocument).toFuture().map(_ => s"Специальность с id $specialityId была удалена из базы данных.")
  }

  def updateSpeciality(specialityId: Int, updatedSpeciality: Speciality): Future[String] = {
    val filter = Document("specialityId" -> specialityId)

    val specialityDocument = BsonDocument(
      "$set" -> BsonDocument(
        "specialityId" -> BsonInt32(updatedSpeciality.specialityId),
        "name" -> BsonString(updatedSpeciality.name)
      )
    )

    MongoDBConnection.specialityCollection.updateOne(filter, specialityDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о специальности с id $specialityId была успешно обновлена."
      } else {
        s"Обновление информации о специальности с id $specialityId не выполнено. Возможно, специальность не найдена или произошла ошибка в базе данных."
      }
    }
  }
}