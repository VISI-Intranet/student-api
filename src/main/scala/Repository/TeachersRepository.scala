package repository

import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Connection._
import Model._

class TeachersRepository(implicit ec: ExecutionContext) {

  def getAllTeachers(): Future[List[Teachers]] = {
    val futureTeachers = MongoDBConnection.teachersCollection.find().toFuture()

    futureTeachers.map { docs =>
      Option(docs).map(_.map { doc =>
        Teachers(
          id = doc.getInteger("id"),
          fullName = doc.getString("fullName"),
          gender = doc.getString("gender"),
          address = doc.getString("address"),
          email = doc.getString("email"),
          phoneNumber = doc.getInteger("phoneNumber")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getTeachersById(teachersId: Int): Future[Option[Teachers]] = {
    val teachersDocument = Document("id" -> teachersId)

    MongoDBConnection.teachersCollection.find(teachersDocument).headOption().map {
      case Some(doc) =>
        Some(
          Teachers(
            id = doc.getInteger("id"),
            fullName = doc.getString("fullName"),
            gender = doc.getString("gender"),
            address = doc.getString("address"),
            email = doc.getString("email"),
            phoneNumber = doc.getInteger("phoneNumber")
          )
        )
      case None => None
    }
  }

  def addTeachers(teachers: Teachers): Future[String] = {
    val teachersDocument = BsonDocument(
      "id" -> BsonInt32(teachers.id),
      "fullName" -> BsonString(teachers.fullName),
      "gender" -> BsonString(teachers.gender),
      "address" -> BsonString(teachers.address),
      "email" -> BsonString(teachers.email),
      "phoneNumber" -> BsonInt32(teachers.phoneNumber)
    )

    MongoDBConnection.teachersCollection.insertOne(teachersDocument).toFuture().map(_ => s"Преподаватель ${teachers.fullName} был добавлен в базу данных.")
  }

  def deleteTeachers(teachersId: Int): Future[String] = {
    val teachersDocument = Document("id" -> teachersId)
    MongoDBConnection.teachersCollection.deleteOne(teachersDocument).toFuture().map(_ => s"Преподаватель с id $teachersId был удален из базы данных.")
  }

  def updateTeachers(teachersId: Int, updatedTeachers: Teachers): Future[String] = {
    val filter = Document("id" -> teachersId)

    val teachersDocument = BsonDocument(
      "$set" -> BsonDocument(
        "id" -> BsonInt32(updatedTeachers.id),
        "fullName" -> BsonString(updatedTeachers.fullName),
        "gender" -> BsonString(updatedTeachers.gender),
        "address" -> BsonString(updatedTeachers.address),
        "email" -> BsonString(updatedTeachers.email),
        "phoneNumber" -> BsonInt32(updatedTeachers.phoneNumber)
      )
    )

    MongoDBConnection.teachersCollection.updateOne(filter, teachersDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о преподавателе с id $teachersId была успешно обновлена."
      } else {
        s"Обновление информации о преподавателе с id $teachersId не выполнено. Возможно, преподаватель не найден или произошла ошибка в базе данных."
      }
    }
  }
}
