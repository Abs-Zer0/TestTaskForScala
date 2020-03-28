package models

import play.api.libs.json
import play.api.libs.json._

import scala.collection.mutable

object Main {

	case class TelNumber(name: String, tel: String)

	val telBook = mutable.HashMap.empty[Long, TelNumber]

	implicit val telNumberWrites = new Writes[(Long, TelNumber)] {
		def writes(el: (Long, TelNumber)) = Json.obj(
			"id" -> el._1,
			"name" -> el._2.name,
			"number" -> el._2.tel)
	}

	implicit val telNumberReads = new Reads[TelNumber] {
		def reads(json: JsValue): JsResult[TelNumber] = {
			val name = (json \ "name").validate[String]
			val tel = (json \ "number").validate[String]

			if(name.isError|| tel.isError)
				JsError()

			JsSuccess(new TelNumber(name.get, tel.get))
		}
	}
}
