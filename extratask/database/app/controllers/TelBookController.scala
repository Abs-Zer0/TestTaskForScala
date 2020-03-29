package controllers

import java.security.InvalidParameterException
import javax.inject._
import models._
import models.Main.{TelNumber, db, telBook}
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Await
import scala.concurrent.duration._
import slick.jdbc.H2Profile.api._

@Singleton class TelBookController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

	def addTel(): Action[JsValue] = Action(parse.json) { request =>
		try {
			val tel = Main.jsonToTelNumber(request.body)
			val future = (telBook returning telBook.map(_.id)) += TelNumber(0, tel.name, tel.number)
			Await.result(db.run(future), Duration.Inf) match {
				case 0 => BadRequest("Error on the server")
				case _ => Ok("Note has added")
			}
		} catch {
			case param: InvalidParameterException => BadRequest("Invalid syntax of name or number")
		}
	}

	def getAllTel(): Action[AnyContent] = Action {
		val res: Seq[TelNumber] = Await.result(db.run(telBook.result), Duration.Inf)
		Ok(Main.bookToJson(res.map(el => (el.id, el.name, el.number))).toString())
	}

	def changeNumberOrName(id: Int): Action[JsValue] = Action(parse.json) { request =>
		try {
			val tel = Main.jsonToTelNumber(request.body)
			val future = telBook.filter(_.id === id).map(el => (el.name, el.number)).update((tel.name, tel.number))
			Await.result(db.run(future), Duration.Inf) match {
				case 0 => BadRequest("Error on the server")
				case _ => Ok("Note has changed")
			}
		} catch {
			case param: InvalidParameterException => BadRequest("Invalid syntax of name or number")
		}
	}

	def deleteTel(id: Int): Action[AnyContent] = Action {
		val res = Await.result(db.run(telBook.filter(_.id === id).delete), Duration.Inf)
		res match {
			case 0 => BadRequest("Error on the server")
			case _ => Ok("Note with \"" + id.toString + "\" id has deleted")
		}
	}

	def searchByName(name: String): Action[AnyContent] = Action {
		val future = telBook.filter(_.name like "%" + name + "%").result
		val res: Seq[(Int, String, String)] = Await.result(db.run(future), Duration.Inf).map(el => (el.id, el.name, el.number))
		Ok(Main.bookToJson(res).toString())
	}

	def searchByNumber(number: String): Action[AnyContent] = Action {
		val future = telBook.filter(_.number like "%" + number + "%").result
		val res: Seq[(Int, String, String)] = Await.result(db.run(future), Duration.Inf).map(el => (el.id, el.name, el.number))
		Ok(Main.bookToJson(res).toString())
	}
}
