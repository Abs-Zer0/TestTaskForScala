package models

import java.security.InvalidParameterException
import play.api.libs.json._
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

object Main {

	case class TelNumber(id: Int, name: String, number: String)

	class TelNumberTable(tag: Tag) extends Table[TelNumber](tag, "telBook") {

		def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

		def name: Rep[String] = column[String]("name")

		def number: Rep[String] = column[String]("number")

		override def * : ProvenShape[TelNumber] = (id, name, number) <> (TelNumber.tupled, TelNumber.unapply)
	}

	val telBook = TableQuery[TelNumberTable]
	val db = Database.forURL("jdbc:h2:tcp://localhost/~/test;DB_CLOSE_DELAY=-1", "admin", "admin", driver = "org.h2.Driver", executor = AsyncExecutor("test", numThreads = 10, queueSize = 1000))
	val setup = DBIO.seq(telBook.schema.createIfNotExists)
	val action = db.run(setup)

	implicit val telNumberWrites: Writes[(Int, String, String)] = (el: (Int, String, String)) =>
		Json.obj("id" -> el._1, "name" -> el._2, "number" -> el._3)

	implicit val telNumberReads: Reads[TelNumber] = (json: JsValue) => {
		val name = (json \ "name").validate[String]
		val tel = (json \ "number").validate[String]
		if (name.isError || tel.isError) JsError() else JsSuccess(TelNumber(0, name.get, tel.get))
	}

	def jsonToTelNumber(arg: JsValue): TelNumber = {
		val jsres = Json.fromJson[TelNumber](arg)(Main.telNumberReads)
		if (jsres.isError) throw new InvalidParameterException("Invalid format \"name\" or \"number\"")
		jsres.get
	}

	def bookToJson(book: Seq[(Int, String, String)]) = book.map(el => Json.toJson(el)).foldLeft(Json.arr())(_ :+ _)
}
