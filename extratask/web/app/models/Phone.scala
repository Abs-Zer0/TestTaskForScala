package models

import java.security.InvalidParameterException

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

object Phone {
	case class Phone(id: Int, name: String, number: String)

	implicit val phoneWrites: Writes[(Int, String, String)] = (el: (Int, String, String)) =>
		Json.obj("id" -> el._1, "name" -> el._2, "number" -> el._3)

	implicit val phoneReads: Reads[Phone] = (json: JsValue) => {
		val name = (json \ "name").validate[String]
		val tel = (json \ "number").validate[String]
		if (name.isError || tel.isError) JsError() else JsSuccess(Phone(0, name.get, tel.get))
	}

	def jsonToPhone(arg: JsValue): Phone = {
		val jsres = Json.fromJson[Phone](arg)(phoneReads)
		if (jsres.isError) throw new InvalidParameterException("Invalid format \"name\" or \"number\"")
		jsres.get
	}

	val phoneForm = Form(
		mapping(
			"id" -> number,
			"name" -> text,
			"number" -> text)
		(Phone.apply)(Phone.unapply))
}
