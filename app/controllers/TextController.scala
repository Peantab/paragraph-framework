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
class TextController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index() = Action {
    implicit request => val xml : UserXML = xmlForm.bindFromRequest().get
    val p = scala.xml.XML.loadString(xml.xml)
    val pageId = (p \ "state" \ "page-id").text
    val title = (p \ "header" \ "title").text
    val texts = p \ "text"
    var text = texts
    for (e <- texts){
      if ((e \ "@page-id").text == pageId){
        text = e
      }
    }
    val header1 = (text \ "header1").text
    val header2 = (text \ "header2").text
    val tag = (text \ "@tag").text
    val default = (text \ "@default").text
    val accept = "> " + (text \ "@accept").text
    val nextPage = (text \ "next").text

    Ok(views.html.main(title)(content = views.html.text(header1, header2, tag, default, accept, nextPage, xml.xml)))
  }

  def attributeEquals(name: String, value: Text)(node: Node) = node.attributes.exists(_ == value)

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}