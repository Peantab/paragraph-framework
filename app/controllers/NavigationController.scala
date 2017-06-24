package controllers

import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

/**
  * Created by Paweł Taborowski on 23.06.17.
  */
class NavigationController @Inject() (ws: WSClient) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  def navigate() = Action.async
  {
    implicit request =>
      val xml : UserXML = xmlForm.bindFromRequest().get
    val code = scala.xml.XML.loadString(xml.xml)
    val pageId = (code \ "state" \ "page-id").text

    val selections = code \ "selection"
    val selectionNumbers = selections.map(i => (i \ "@page-id").text)

    val texts = code \ "text"
    val textNumbers = texts.map(i => (i \ "@page-id").text)

    val paragraphs = code \ "paragraph"
    val paragraphNumbers = paragraphs.map(i => (i \ "@page-id").text)

    implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

    if (selectionNumbers.contains(pageId)){
      ws.url("http://" + request.host + "/selection/").post(Map("xml" -> Seq(xml.xml))).map { response =>
        Ok(Html(response.body))
      }
    }else if(textNumbers.contains(pageId)) {
      ws.url("http://" + request.host + "/text/").post(Map("xml" -> Seq(xml.xml))).map { response =>
        Ok(response.body)
      }
    }else if(paragraphNumbers.contains(pageId)) {
      ws.url("http://" + request.host + "/paragraph/").post(Map("xml" -> Seq(xml.xml))).map { response =>
        Ok(response.body)
      }
    }else {
      ws.url("http://" + request.host + "/").get().map { response =>
        Ok(Html(response.body))
      }
    }
  }

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}
