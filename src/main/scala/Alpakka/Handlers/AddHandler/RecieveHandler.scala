package Alpakka.Handlers.AddHandler

import Alpakka.Operations.SendMessageWithCorrelationIdAlpakka
import Alpakka.RabbitMQModel.RabbitMQModel
import Repository.AdditionalRepo.Repo.getNameOfStudentById
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, AmqpLocalConnectionProvider}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object RecieveHandler {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit lazy val system: ActorSystem = ActorSystem("web-system")
  implicit lazy val mat: Materializer = Materializer(system)

  val handler: (String, String) => Unit = (message, routingKey) => {

    routingKey match {

      case "univer.teacher-api.studentsByIdGet" =>
        // Обработка для ключа "key1"
        println(s"Hello fucking shit")
      case "univer.event-api.notficationEventForStudentPost" =>
        // Обработка для ключа "key2"
        println(message)
      case "univer.debt-api.studentNameForDebtByIdGet" =>
        val nameFuture: Future[Option[String]] = getNameOfStudentById(message.toInt)

        val replyStudentNameForDebtMQModel: RabbitMQModel = RabbitMQModel("DebtSubscription", "UniverSystem", "univer.student-api.studentNameForDebtByIdGet")

        val amqpConnectionProvider :AmqpConnectionProvider = AmqpLocalConnectionProvider

        nameFuture.onComplete {
          case Success(Some(name)) => println(s"Имя студента: $name")
            SendMessageWithCorrelationIdAlpakka.sendMessageWithCorrelationId(name,replyStudentNameForDebtMQModel,amqpConnectionProvider)()
          case Failure(exception) => println(s"Ошибка при получении имени студента: ${exception.getMessage}")
        }


      case _ =>
        // Обработка для всех остальных случаев
        println(s"#")
    }

  }

}
