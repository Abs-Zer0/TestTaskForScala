package models

import models.Phone.Phone
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

object PhoneBook {

	class PhoneTable(tag: Tag) extends Table[Phone](tag, "telBook") {

		def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

		def name: Rep[String] = column[String]("name")

		def number: Rep[String] = column[String]("number")

		override def * : ProvenShape[Phone] = (id, name, number) <> (Phone.tupled, Phone.unapply)
	}

	def phonebookToJson(book: Seq[(Int, String, String)]) = book.map(el => Json.toJson(el)).foldLeft(Json.arr())(_ :+ _)

	val phonebook = TableQuery[PhoneTable]
}
