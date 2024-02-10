package Connection

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}



object MongoDBConnection
{
  private val mongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("Student")
  val studentCollection: MongoCollection[Document] = database.getCollection("Student")
  val cafedraCollection: MongoCollection[Document] = database.getCollection("Cafedra")
  val faculityCollection: MongoCollection[Document] = database.getCollection("Faculity")
  val specialityCollection: MongoCollection[Document] = database.getCollection("Speciality")
  val teachersCollection: MongoCollection[Document] = database.getCollection("Teachers")


}