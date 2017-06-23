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
  def index(xml : String) = Action {
    val p = scala.xml.XML.loadString(xml)
    val title = (p \ "header" \ "title").text
    val button = (p \ "title-screen" \ "button").text
    val blurb = (p \ "title-screen" \ "blurb").text
    Ok(views.html.main(title)(content = views.html.welcome(title, button, blurb, xml)))
  }
}
