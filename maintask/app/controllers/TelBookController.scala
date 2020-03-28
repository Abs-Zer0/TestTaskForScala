package controllers

import javax.inject._
import models.Main.TelNumber
import play.api.mvc._
import play.api.libs.json._
import models._
import scala.collection.mutable

@Singleton class TelBookController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

	def addTel() = Action(parse.json) { request =>
		val tel = jsonToTelNumber(request.body)
		if (Main.telBook.nonEmpty)
			Main.telBook.put(Main.telBook.max(Ordering.by[(Long, TelNumber), Long](el => el._1))._1 + 1, tel)
		else
			Main.telBook.put(0, tel)

		Ok("Telephone number has writted in the book")
	}

	def getAllTel() = Action {
		Ok(bookToJson(Main.telBook).toString())
	}

	def changeNumberOrName(id: Long) = Action(parse.json) { request =>
		if (!Main.telBook.contains(id))
			BadRequest("Book has not note with this id")

		val tel = jsonToTelNumber(request.body)
		Main.telBook.update(id, tel)

		Ok("Telephone number or name has updated")
	}

	def deleteTel(id: Long) = Action {
		if (!Main.telBook.contains(id)) BadRequest("Book has not note with this id")

		Main.telBook.remove(id)

		Ok("Note with this \"id\" has deleted")
	}

	def searchByName(name: String) = Action {
		val found = Main.telBook.filter(el => el._2.name.contains(name))

		Ok(bookToJson(found).toString())
	}

	def searchByNumber(number: String) = Action {
		val found = Main.telBook.filter(el => el._2.tel.contains(number))

		Ok(bookToJson(found).toString())
	}

	def jsonToTelNumber(arg: JsValue) = {
		val jsres = Json.fromJson[TelNumber](arg)
		if (jsres.isError) BadRequest("Invalid format \"name\" or \"number\"")
		jsres.get
	}

	def bookToJson(book: mutable.HashMap[Long, TelNumber]) = book.map(el => Json.toJson(el)).foldLeft(Json.arr())(_ :+ _)
}
