package controllers

import javax.inject._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
/**
  * Created by Paweł Taborowski on 23.05.17.
  */
@Singleton
class GameController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index(xml : String) = Action {
    val p = scala.xml.XML.loadString(xml)
    val title = (p \ "header" \ "title").text
    Ok(views.html.main("Placeholder")(views.html.welcome(title)))
  }
}
