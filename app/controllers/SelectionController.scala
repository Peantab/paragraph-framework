package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.xml.{Node, Text}

/**
  * Created by Paweł Taborowski on 23.06.17.
  */
class SelectionController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index() = Action {
    implicit request => val xml : UserXML = xmlForm.bindFromRequest().get
    val p = scala.xml.XML.loadString(xml.xml)
    val pageId = (p \ "state" \ "page-id").text
    val title = (p \ "header" \ "title").text
    val selections = p \ "selection"
    var selection = selections
    for (e <- selections){
      if ((e \ "@page-id").text == pageId){
        selection = e
      }
    }
//    val selection = (p \\ "_").filter(attributeEquals("@page-id", Text(pageId)))
    val header1 = (selection \ "header1").text
    val header2 = (selection \ "header2").text

    val options = selection \ "option"
    var optionsCode : String = ""
    for (e <- options){
      val mapToEncode = Map("page-id"->(selection \ "next").text, (selection \ "@tag")-> (e \ "@short"))
      var stringMap = ""
      for ((k, v) <- mapToEncode) {
        stringMap = stringMap + k + "=" + v + ":"
      }
      optionsCode += "<form method=\"POST\" action=\"/bind/\" style=\"text-align: center\">" +
        "<input type=\"text\" name=\"xml\" value=\"" + scala.xml.Utility.escape(xml.xml) + "\" style=\"display: none\" />" +
        "<input type=\"text\" name=\"map\" value=\"" + stringMap + "\" style=\"display: none\" />" +
        "<input type=\"submit\" value=\"> " + (e \ "@value").text + "\" class=\"answer\" onMouseOver=\"document.getElementById('description').innerHTML = '" + e.text + "';\" onMouseOut=\"document.getElementById('description').innerHTML = '';\"/>" +
        "</form>"
    }

    Ok(views.html.main(title)(content = views.html.selection(header1, header2, optionsCode, xml.xml)))
  }

  def attributeEquals(name: String, value: Text)(node: Node) = node.attributes.exists(_ == value)

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}
