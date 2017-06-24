package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.xml.{Node, Text}

/**
  * Created by Paweł Taborowski on 24.06.17.
  */
class ParagraphController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index() = Action {
    implicit request => val xml : UserXML = xmlForm.bindFromRequest().get
    val p = scala.xml.XML.loadString(xml.xml)
    val pageId = (p \ "state" \ "page-id").text
    val title = (p \ "header" \ "title").text
    val paragraphs = p \ "paragraph"
    var paragraph = paragraphs
    for (e <- paragraphs){
      if ((e \ "@page-id").text == pageId){
        paragraph = e
      }
    }
    val header1 = (paragraph \ "header1").text
    val header2 = (paragraph \ "header2").text
    val picture = (paragraph \ "picture").text
    val description = (paragraph \ "description").text
    val answers = "Kiedyś"

    Ok(views.html.main(title)(content = views.html.paragraph(header1, header2, picture, description, answers, xml.xml)))
  }

  def attributeEquals(name: String, value: Text)(node: Node) = node.attributes.exists(_ == value)

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}