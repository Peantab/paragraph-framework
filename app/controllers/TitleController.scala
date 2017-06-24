package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
/**
  * Created by Paweł Taborowski on 23.05.17.
  */
@Singleton
class TitleController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index() = Action { implicit request =>
    val fetch : UserXML = xmlForm.bindFromRequest().get
    val p = scala.xml.XML.loadString(fetch.xml)
    val title = (p \ "header" \ "title").text
    val button = (p \ "title-screen" \ "button").text
    val blurb = (p \ "title-screen" \ "blurb").text
    Ok(views.html.main(title)(content = views.html.welcome(title, button, blurb, fetch.xml)))
  }

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}

case class UserXML(xml: String)