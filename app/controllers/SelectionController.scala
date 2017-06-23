package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.xml.{Node, Text}

/**
  * Created by Paweł Taborowski on 23.06.17.
  */
class SelectionController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index(xml : String) = Action {
    val p = scala.xml.XML.loadString(xml)
    val pageId = (p \ "state" \ "page-id").text
    val title = (p \ "header" \ "title").text
    val selections = (p \ "selection" )
    var selection = selections
    for (e <- selections){
      if ((e \ "@page-id").text == pageId){
        selection = e
      }
    }
//    val selection = (p \\ "_").filter(attributeEquals("@page-id", Text(pageId)))
    val header1 = (selection \ "header1").text
    val header2 = (selection \ "header2").text
    Ok(views.html.main(title)(content = views.html.welcome(title, header1, header2, xml)))
  }

  def attributeEquals(name: String, value: Text)(node: Node) = node.attributes.exists(_ == value)

}
