package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.CafedraRepository
import Model._

class CafedraRoutes(implicit val cafedraRepository: CafedraRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("cafedra") {
      concat(
        pathEnd {
          concat(
            get {
              complete(cafedraRepository.getAllCafedras())
            },
            post {
              entity(as[Cafedra]) { cafedra =>
                complete(cafedraRepository.addCafedra(cafedra))
              }
            }
          )
        },
        path(IntNumber) { cafedraId =>
          concat(
            get {
              complete(cafedraRepository.getCafedraById(cafedraId))
            },
            put {
              entity(as[Cafedra]) { updatedCafedra =>
                complete(cafedraRepository.updateCafedra(cafedraId, updatedCafedra))
              }
            },
            delete {
              complete(cafedraRepository.deleteCafedra(cafedraId))
            }
          )
        }
      )
    }
}
