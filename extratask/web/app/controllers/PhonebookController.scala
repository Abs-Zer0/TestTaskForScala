package controllers

import javax.inject.Inject
import models.Main._
import models.Phone._
import models.PhoneBook._
import play.api.data.Form
import play.api.mvc._
import slick.jdbc.H2Profile.api._
import scala.concurrent.Await
import scala.concurrent.duration.Duration


class PhonebookController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

	def createPhonePost() = Action { implicit request: MessagesRequest[AnyContent] =>
		val errorFunction = { formWithErrors: Form[Phone] =>
			Redirect(routes.PhonebookController.getAllPhones())
		}
		val successFunction = { data: Phone =>
			val future = (phonebook returning phonebook.map(_.id)) += data
			Await.result(db.run(future), Duration.Inf) match {
				case 0 => Redirect(routes.PhonebookController.getAllPhones()).flashing("error" -> "Ошибка на сервере")
				case _ => Redirect(routes.PhonebookController.getAllPhones()).flashing("info" -> "Запись добавлена")
			}
		}
		val formValidationResult = phoneForm.bindFromRequest
		formValidationResult.fold(errorFunction, successFunction)
	}

	def createPhone = Action { implicit request: MessagesRequest[AnyContent] =>
		val postUrl = routes.PhonebookController.createPhonePost()
		Ok(views.html.createOrChangePhone("Создать", phoneForm, postUrl))
	}

	def getAllPhones() = Action { implicit request: MessagesRequest[AnyContent] =>
		val res: Seq[Phone] = Await.result(db.run(phonebook.result), Duration.Inf)
		//val postUrl = routes.PhonebookController.searchPhone()
		Ok(views.html.phones(res, phoneForm))
	}

	def changeNumberOrNamePost(id: Int) = Action { implicit request: MessagesRequest[AnyContent] =>
		val errorFunction = { formWithErrors: Form[Phone] =>
			Redirect(routes.PhonebookController.getAllPhones())
		}
		val successFunction = { data: Phone =>
			val future = phonebook.filter(_.id === id).map(el => (el.name, el.number)).update((data.name, data.number))
			Await.result(db.run(future), Duration.Inf) match {
				case 0 => Redirect(routes.PhonebookController.getAllPhones()).flashing("error" -> "Ошибка на сервере")
				case _ => Redirect(routes.PhonebookController.getAllPhones()).flashing("info" -> "Запись изменена")
			}
		}
		val formValidationResult = phoneForm.bindFromRequest
		formValidationResult.fold(errorFunction, successFunction)
	}

	def changeNumberOrName(id: Int) = Action { implicit request: MessagesRequest[AnyContent] =>
		val postUrl = routes.PhonebookController.changeNumberOrNamePost(id)
		Ok(views.html.createOrChangePhone("Изменить", phoneForm, postUrl))
	}

	def deletePhone(id: Int): Action[AnyContent] = Action {
		val res = Await.result(db.run(phonebook.filter(_.id === id).delete), Duration.Inf)
		res match {
			case 0 => BadRequest("Error on the server") //case _ => Ok("Note with \"" + id.toString + "\" id has deleted")
			case _ => Redirect(routes.PhonebookController.getAllPhones()).flashing("info" -> "Запись была удалена")
		}
	}

	def searchPhone(name: String="", number:String="") = Action { implicit request: MessagesRequest[AnyContent] =>
		val errorFunction = { formWithErrors: Form[Phone] =>
			Redirect(routes.PhonebookController.getAllPhones())
		}
		val successFunction = { data: Phone =>
			val likeName = "%" + data.name + "%"
			val likeNumber = "%" + data.number + "%"
			val future = phonebook.filter(el => el.name.like(likeName) && el.number.like(likeNumber)).result
			val res: Seq[Phone] = Await.result(db.run(future), Duration.Inf).map(el => Phone(el.id, el.name, el.number))
			Ok(views.html.phones(res, phoneForm, name, number))
		}
		val formValidationResult = phoneForm.bindFromRequest
		formValidationResult.fold(errorFunction, successFunction)
	}
}
