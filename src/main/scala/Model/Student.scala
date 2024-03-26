package Model
case class Student(
                    studentId: Int,
                    name:String,
                    date_of_birth:String,
                    address:String,
                    phone_number:String,
                    got_postuplenie:String,
                    faculity: String,
                    cafedra:String,
                    course: List[String],
                    group:String,
                    ball_ent:Int,
                    status: List[String],
                    grodanstvo:String,
                     gender:List[String],
                    english_language_level:String,
                    specialization: String,
                  )