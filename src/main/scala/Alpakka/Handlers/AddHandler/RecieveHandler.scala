package Alpakka.Handlers.AddHandler

object RecieveHandler {

  val handler: (String, String) => Unit = (message, routingKey) => {

    routingKey match {
      case "univer.teacher-api.studentsByIdGet" =>
        // Обработка для ключа "key1"
        println(s"Hello fucking shit")
      case "univer.event-api.notficationEventForStudentPost" =>
        // Обработка для ключа "key2"
        println(message)
      case _ =>
        // Обработка для всех остальных случаев
        println(s"#")
    }

  }

}
