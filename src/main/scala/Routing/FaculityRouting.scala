package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.FaculityRepository
import Model._

class FaculityRoutes(implicit val faculityRepository: FaculityRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("faculity") {
      concat(
        pathEnd {
          concat(
            get {
              complete(faculityRepository.getAllFaculties())
            },
            post {
              entity(as[Faculity]) { faculity =>
                complete(faculityRepository.addFaculity(faculity))
              }
            }
          )
        },
        path(IntNumber) { faculityId =>
          concat(
            get {
              complete(faculityRepository.getFaculityById(faculityId))
            },
            put {
              entity(as[Faculity]) { updatedFaculity =>
                complete(faculityRepository.updateFaculity(faculityId, updatedFaculity))
              }
            },
            delete {
              complete(faculityRepository.deleteFaculity(faculityId))
            }
          )
        }
      )
    }
}
