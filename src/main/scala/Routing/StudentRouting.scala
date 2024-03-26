package Routing

import Alpakka.Operations.SendMessageAndWaitForResponsAlpakka
import Alpakka.RabbitMQModel.RabbitMQModel
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.StudentRepository
import Model._
import RabbitMQ.RabbitMQOperation.Operations.Formatter.extractContentList
import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.stream.Materializer
import akka.stream.alpakka.amqp.AmqpConnectionProvider

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class StudentRoutes(implicit val studentRepository: StudentRepository , amqpConnectionProvider: AmqpConnectionProvider) extends Json4sSupport {

  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  implicit lazy val system: ActorSystem = ActorSystem("web-system")
  implicit lazy val mat: Materializer = Materializer(system)
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val pubStudentDisciplineMQModel: RabbitMQModel = RabbitMQModel("StudentPublisher", "UniverSystem", "univer.student-api.disciplinesForStudentByIdGet")
  val replyStudentDisciplineMQModel: RabbitMQModel = RabbitMQModel("StudentSubscription", "UniverSystem", "univer.discipline-api.disciplinesForStudentByIdGet")

  val route =
    pathPrefix("student") {
      concat(
        pathPrefix("studentDisciplines") {
          path(Segment) { studentId =>
            concat(
              get {
                val studentFuture: Future[Option[Student]] = studentRepository.getStudentById(studentId.toInt)

                val resultFuture: Future[Option[Student]] = studentFuture.flatMap {
                  case Some(student) =>
                    val sendResultFuture = SendMessageAndWaitForResponsAlpakka.sendMessageAndWaitForResponse(extractContentList(student.disciplineId), pubStudentDisciplineMQModel, replyStudentDisciplineMQModel, amqpConnectionProvider)()
                    sendResultFuture.map { result =>


                      val resultList = result.stripPrefix("List(").stripSuffix(")").split(",").map(_.trim).toList

                      println(resultList)


                      val updatedStudent = student.copy(disciplines = Option(resultList))

                      Some(updatedStudent)
                    }

                }

                onSuccess(resultFuture) {
                  case Some(teacher) => complete(teacher)
                }
              }
            )
          }
        },
        // Обработка запросов для получения всех студентов или определенного студента по его идентификатору
        pathEnd {
          concat(
            // Обработка запросов для получения всех студентов
            get {
              complete(studentRepository.getAllStudents())
            },
            // Обработка запросов для обновления информации о студенте
            put {
              path(IntNumber) { studentId =>
                entity(as[Student]) { updatedStudent =>
                  complete(studentRepository.updateStudent(studentId, updatedStudent))
                }
              }
            },
            // Обработка запросов для удаления студента по его идентификатору
            delete {
              path(IntNumber) { studentId =>
                complete(studentRepository.deleteStudent(studentId))
              }
            }
          )
        }
      )
    }

}