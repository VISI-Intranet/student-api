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

class StudentRepository(implicit ec: ExecutionContext) {

  def getAllStudents(): Future[List[Student]] = {
    val futureStudents = MongoDBConnection.studentCollection.find().toFuture()

    futureStudents.map { docs =>
      Option(docs).map(_.map { doc =>
        Student(
          studentId = doc.getInteger("studentId"),
          name = doc.getString("name"),
          date_of_birth = doc.getString("date_of_birth"),
          address = doc.getString("address"),
          phone_number = doc.getString("phone_number"),
          got_postuplenie = doc.getString("got_postuplenie"),
          faculity = doc.getString("faculity"),
          cafedra = doc.getString("cafedra"),
          course = Option(doc.getList("course", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          group = doc.getString("group"),
          ball_ent = doc.getInteger("ball_ent"),
          status = Option(doc.getList("status", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          grodanstvo = doc.getString("grodanstvo"),
          gender = Option(doc.getList("gender", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          english_language_level = doc.getString("english_language_level"),
          specialization = doc.getString("specialization")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getStudentById(studentId: Int): Future[Option[Student]] = {
    val studentDocument = Document("studentId" -> studentId)

    MongoDBConnection.studentCollection.find(studentDocument).headOption().map {
      case Some(doc) =>
        Some(
          Student(
            studentId = doc.getInteger("studentId"),
            name = doc.getString("name"),
            date_of_birth = doc.getString("date_of_birth"),
            address = doc.getString("address"),
            phone_number = doc.getString("phone_number"),
            got_postuplenie = doc.getString("got_postuplenie"),
            faculity = doc.getString("faculity"),
            cafedra = doc.getString("cafedra"),
            course = Option(doc.getList("course", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            group = doc.getString("group"),
            ball_ent = doc.getInteger("ball_ent"),
            status = Option(doc.getList("status", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            grodanstvo = doc.getString("grodanstvo"),
            gender = Option(doc.getList("gender", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            english_language_level = doc.getString("english_language_level"),
            specialization = doc.getString("specialization")
          )
        )
      case None => None
    }
  }

  def Studentfilter(param: String): Future[List[Student]] = {
    val keyValue = param.split("=")
    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = keyValue(1)
      val facultyDocument = Document(key -> value)
      MongoDBConnection.studentCollection
        .find(facultyDocument)
        .toFuture()
        .map { docs =>
          docs.map { doc =>
            Student(
              studentId = doc.getInteger("studentId"),
              name = doc.getString("name"),
              date_of_birth = doc.getString("date_of_birth"),
              address = doc.getString("address"),
              phone_number = doc.getString("phone_number"),
              got_postuplenie = doc.getString("got_postuplenie"),
              faculity = doc.getString("faculity"),
              cafedra = doc.getString("cafedra"),
              course = Option(doc.getList("course", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
              group = doc.getString("group"),
              ball_ent = doc.getInteger("ball_ent"),
              status = Option(doc.getList("status", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
              grodanstvo = doc.getString("grodanstvo"),
              gender = Option(doc.getList("gender", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
              english_language_level = doc.getString("english_language_level"),
              specialization = doc.getString("specialization")
            )
          }.toList
        }
    } else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }

  def addStudent(student: Student): Future[String] = {
    val studentDocument = BsonDocument(
      "studentId" -> BsonInt32(student.studentId),
      "name" -> BsonString(student.name),
      "date_of_birth" -> BsonString(student.date_of_birth),
      "address" -> BsonString(student.address),
      "phone_number" -> BsonString(student.phone_number),
      "got_postuplenie" -> BsonString(student.got_postuplenie),
      "faculity" -> BsonString(student.faculity),
      "cafedra" -> BsonString(student.cafedra),
      "course" -> BsonArray(student.course.map(BsonString(_))),
      "group" -> BsonString(student.group),
      "ball_ent" -> BsonInt32(student.ball_ent),
      "status" -> BsonArray(student.status.map(BsonString(_))),
      "grodanstvo" -> BsonString(student.grodanstvo),
      "gender" -> BsonArray(student.gender.map(BsonString(_))),
      "english_language_level" -> BsonString(student.english_language_level),
      "specialization" -> BsonString(student.specialization)
    )

    MongoDBConnection.studentCollection.insertOne(studentDocument).toFuture().map(_ => s"Студент ${student.name} был добавлен в базу данных.")
  }

  def deleteStudent(studentId: Int): Future[String] = {
    val studentDocument = Document("studentId" -> studentId)
    MongoDBConnection.studentCollection.deleteOne(studentDocument).toFuture().map(_ => s"Студент с id $studentId был удален из базы данных.")
  }

  def updateStudent(studentId: Int, updatedStudent: Student): Future[String] = {
    val filter = Document("studentId" -> studentId)

    val studentDocument = BsonDocument(
      "$set" -> BsonDocument(
        "studentId" -> BsonInt32(updatedStudent.studentId),
        "name" -> BsonString(updatedStudent.name),
        "date_of_birth" -> BsonString(updatedStudent.date_of_birth),
        "address" -> BsonString(updatedStudent.address),
        "phone_number" -> BsonString(updatedStudent.phone_number),
        "got_postuplenie" -> BsonString(updatedStudent.got_postuplenie),
        "faculity" -> BsonString(updatedStudent.faculity),
        "cafedra" -> BsonString(updatedStudent.cafedra),
        "course" -> BsonArray(updatedStudent.course.map(BsonString(_))),
        "group" -> BsonString(updatedStudent.group),
        "ball_ent" -> BsonInt32(updatedStudent.ball_ent),
        "status" -> BsonArray(updatedStudent.status.map(BsonString(_))),
        "grodanstvo" -> BsonString(updatedStudent.grodanstvo),
        "gender" -> BsonArray(updatedStudent.gender.map(BsonString(_))),
        "english_language_level" -> BsonString(updatedStudent.english_language_level),
        "specialization" -> BsonString(updatedStudent.specialization)
      )
    )

    MongoDBConnection.studentCollection.updateOne(filter, studentDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о студенте с id $studentId была успешно обновлена."
      } else {
        s"Обновление информации о студенте с id $studentId не выполнено. Возможно, студент не найден или произошла ошибка в базе данных."
      }
    }
  }
}