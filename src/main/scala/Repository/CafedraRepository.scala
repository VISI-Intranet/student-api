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

class CafedraRepository(implicit ec: ExecutionContext) {

  def getAllCafedras(): Future[List[Cafedra]] = {
    val futureCafedras = MongoDBConnection.cafedraCollection.find().toFuture()

    futureCafedras.map { docs =>
      Option(docs).map(_.map { doc =>
        Cafedra(
          id = doc.getInteger("id"),
          cafedraName = doc.getString("cafedraName"),
          dekanCafedri = doc.getString("dekanCafedri"),
          listTechears = Option(doc.getList("listTechears", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          listStudent = Option(doc.getList("listStudent", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          listSpecialty = Option(doc.getList("listSpecialty", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getCafedraById(cafedraId: Int): Future[Option[Cafedra]] = {
    val cafedraDocument = Document("id" -> cafedraId)

    MongoDBConnection.cafedraCollection.find(cafedraDocument).headOption().map {
      case Some(doc) =>
        Some(
          Cafedra(
            id = doc.getInteger("id"),
            cafedraName = doc.getString("cafedraName"),
            dekanCafedri = doc.getString("dekanCafedri"),
            listTechears = Option(doc.getList("listTechears", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            listStudent = Option(doc.getList("listStudent", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            listSpecialty = Option(doc.getList("listSpecialty", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
          )
        )
      case None => None
    }
  }

  def addCafedra(cafedra: Cafedra): Future[String] = {
    val cafedraDocument = BsonDocument(
      "id" -> BsonInt32(cafedra.id),
      "cafedraName" -> BsonString(cafedra.cafedraName),
      "dekanCafedri" -> BsonString(cafedra.dekanCafedri),
      "listTechears" -> BsonArray(cafedra.listTechears.map(BsonString(_))),
      "listStudent" -> BsonArray(cafedra.listStudent.map(BsonString(_))),
      "listSpecialty" -> BsonArray(cafedra.listSpecialty.map(BsonString(_)))
    )

    MongoDBConnection.cafedraCollection.insertOne(cafedraDocument).toFuture().map(_ => s"Кафедра ${cafedra.cafedraName} была добавлена в базу данных.")
  }

  def deleteCafedra(cafedraId: Int): Future[String] = {
    val cafedraDocument = Document("id" -> cafedraId)
    MongoDBConnection.cafedraCollection.deleteOne(cafedraDocument).toFuture().map(_ => s"Кафедра с id $cafedraId была удалена из базы данных.")
  }

  def updateCafedra(cafedraId: Int, updatedCafedra: Cafedra): Future[String] = {
    val filter = Document("id" -> cafedraId)

    val cafedraDocument = BsonDocument(
      "$set" -> BsonDocument(
        "id" -> BsonInt32(updatedCafedra.id),
        "cafedraName" -> BsonString(updatedCafedra.cafedraName),
        "dekanCafedri" -> BsonString(updatedCafedra.dekanCafedri),
        "listTechears" -> BsonArray(updatedCafedra.listTechears.map(BsonString(_))),
        "listStudent" -> BsonArray(updatedCafedra.listStudent.map(BsonString(_))),
        "listSpecialty" -> BsonArray(updatedCafedra.listSpecialty.map(BsonString(_)))
      )
    )

    MongoDBConnection.cafedraCollection.updateOne(filter, cafedraDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о кафедре с id $cafedraId была успешно обновлена."
      } else {
        s"Обновление информации о кафедре с id $cafedraId не выполнено. Возможно, кафедра не найдена или произошла ошибка в базе данных."
      }
    }
  }
}