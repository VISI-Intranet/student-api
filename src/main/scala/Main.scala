import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mongodb.scala.MongoClient
import repository._
import Routing._
import Model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.util.Date

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("MyAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Подключение к базе данных
  val client = MongoClient()
  implicit val db = client.getDatabase("Exam")

  implicit val studentRepository = new StudentRepository()
  implicit val cafedraRepository = new CafedraRepository()
  implicit val faculityRepository = new FaculityRepository()
  implicit val specialityRepository= new SpecialityRepository()
  implicit val teachersRepository= new TeachersRepository()

  val studentRoutes = new StudentRoutes()
  val cafedraRoutes = new CafedraRoutes()
  val faculityRoutes = new FaculityRoutes()
  val specialityRoutes = new SpecialityRoutes()
  val teachersRoutes = new TeachersRoutes()


  // Старт сервера
  private val bindingFuture = Http().bindAndHandle(
    studentRoutes.route  ~ cafedraRoutes.route ~ faculityRoutes.route ~ specialityRoutes.route ~ teachersRoutes.route, // Используйте '~' для объединения роутов
    "localhost",
    8081
  )

  println(s"Server online at http://localhost:8081/")

  // Остановка сервера при завершении приложения
  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}