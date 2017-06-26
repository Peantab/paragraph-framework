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
    val fetch : UserXML = try {xmlForm.bindFromRequest().get } catch { case _ => UserXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?><gamebook><header><title>Error</title></header><title-screen><blurb>Please provide a valid XML (none was provided).</blurb><button>Back to menu</button></title-screen></gamebook>")}
    val p = try { scala.xml.XML.loadString(fetch.xml) } catch { case ex => <gamebook><header><title>Error</title></header><title-screen><blurb>Please provide a valid XML. Current problem: {ex.getMessage}</blurb></title-screen></gamebook> }
    val title = (p \ "header" \ "title").text
    val button = (p \ "title-screen" \ "button").text
    val blurb = (p \ "title-screen" \ "blurb").text
    val author = (p \ "title-screen" \ "author").text
    Ok(views.html.main(title)(content = views.html.welcome(title, button, blurb, author, fetch.xml)))
  }

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}

case class UserXML(xml: String)