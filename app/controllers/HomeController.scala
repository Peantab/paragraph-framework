package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index(xmlForm))
  }

  def button = Action { implicit request =>
    val fetch : UserXML = xmlForm.bindFromRequest().get

    Redirect(routes.TitleController.index(fetch.xml))
  }

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}

case class UserXML(xml: String)
//object HomeController{
//
//}