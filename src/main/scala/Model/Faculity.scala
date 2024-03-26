package Model

case class Faculity(
                    id:Int,
                    name:String,
                    listCafedra:List[String],
                    listStudent:List[String],
                    address:String,
                    achievementsFaculty:List[String],
                    phoneNumber:Int,
                    graduates:List[String],
                    trainingPrograms:List[String],
                    listCouse:List[String],
                  )