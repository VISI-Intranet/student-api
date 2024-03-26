package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.SpecialityRepository
import Model._

class SpecialityRoutes(implicit val specialityRepository: SpecialityRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("speciality") {
      concat(
        pathEnd {
          concat(
            get {
              complete(specialityRepository.getAllSpecialities())
            },
            post {
              entity(as[Speciality]) { speciality =>
                complete(specialityRepository.addSpeciality(speciality))
              }
            }
          )
        },
        path(IntNumber) { specialityId =>
          concat(
            get {
              complete(specialityRepository.getSpecialityById(specialityId))
            },
            put {
              entity(as[Speciality]) { updatedSpeciality =>
                complete(specialityRepository.updateSpeciality(specialityId, updatedSpeciality))
              }
            },
            delete {
              complete(specialityRepository.deleteSpeciality(specialityId))
            }
          )
        }
      )
    }
}
