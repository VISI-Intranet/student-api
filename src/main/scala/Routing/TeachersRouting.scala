package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.TeachersRepository
import Model._

class TeachersRoutes(implicit val teachersRepository: TeachersRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("teachers") {
      concat(
        pathEnd {
          concat(
            get {
              complete(teachersRepository.getAllTeachers())
            },
            post {
              entity(as[Teachers]) { teachers =>
                complete(teachersRepository.addTeachers(teachers))
              }
            }
          )
        },
        path(IntNumber) { teachersId =>
          concat(
            get {
              complete(teachersRepository.getTeachersById(teachersId))
            },
            put {
              entity(as[Teachers]) { updatedTeachers =>
                complete(teachersRepository.updateTeachers(teachersId, updatedTeachers))
              }
            },
            delete {
              complete(teachersRepository.deleteTeachers(teachersId))
            }
          )
        }
      )
    }
}
