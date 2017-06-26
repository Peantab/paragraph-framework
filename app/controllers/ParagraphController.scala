package controllers

import java.lang.NumberFormatException
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
    val description = ParagraphController.injectValues((paragraph \ "description").text, p)

    val answers = paragraph \ "answer"
    var answersCode = ""
    for (e <- answers){
      val condition = (e \ "@condition").text
      if(ParagraphController.cndtn(condition, p)) {
        var mapToEncode: Map[Any, Any] = Map("page-id" -> (e \ "@next").text)
        mapToEncode = mapToEncode ++ ParagraphController.effect((e \ "@effect").text, p)
        var stringMap = ""
        for ((k, v) <- mapToEncode) {
          stringMap = stringMap + k + "=" + v + ":"
        }
        answersCode += "<form method=\"POST\" action=\"/bind/\" style=\"text-align: center\">" +
          "<input type=\"text\" name=\"xml\" value=\"" + scala.xml.Utility.escape(xml.xml) + "\" style=\"display: none\" />" +
          "<input type=\"text\" name=\"map\" value=\"" + stringMap + "\" style=\"display: none\" />" +
          "<input type=\"submit\" value=\"> " + ParagraphController.injectValues(e.text, p) + "\" class=\"answer\" />" +
          "</form>"
      }
    }

    Ok(views.html.main(title)(content = views.html.paragraph(header1, header2, picture, description, answersCode, xml.xml)))
  }

  def attributeEquals(name: String, value: Text)(node: Node) = node.attributes.exists(_ == value)

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}

object ParagraphController {
  def cndtn(cond: String, xml: scala.xml.Elem):Boolean = {
    var oredList: List[Boolean] = List(false)
    for (ored <- cond.split(":")){
      var andedList: List[Boolean] = List(true)
      for (anded <- ored.split("&")){
        if (anded.split("=").length == 2){
          val attributeAndValue = anded.split("=")
          andedList = andedList.::((xml \ "state" \ attributeAndValue(0)).text == attributeAndValue(1))
        }else if(anded.split("<").length == 2){
          val attributeAndValue = anded.split("<")
          val safeNumber = try {(xml \ "state" \ attributeAndValue(0)).text.toInt} catch {
            case _: NumberFormatException => 0
          }
          andedList = andedList.::(safeNumber < attributeAndValue(1).toInt)
        }else if(anded.split(">").length == 2){
          val attributeAndValue = anded.split(">")
          val safeNumber = try {(xml \ "state" \ attributeAndValue(0)).text.toInt} catch {
            case _: NumberFormatException => 0
          }
          andedList = andedList.::(safeNumber > attributeAndValue(1).toInt)
        }
      }
      oredList = oredList.::(andedList.reduce(_&_))
    }
    oredList.reduce(_|_)
  }

  def effect(eff: String, xml: scala.xml.Elem):Map[_,_] = {
    var diffMap: Map[Any, Any] = Map()
    for (anded <- eff.split("&")){
      if (anded.split("=").length == 2){
        val attributeAndValue = anded.split("=")
        diffMap += (attributeAndValue(0) -> attributeAndValue(1))
      }else if(anded.split("@").length == 2){
        val attributeAndValue = anded.split("@")
        val current = try {(xml \ "state" \ attributeAndValue(0)).text.toInt } catch {
          case _: NumberFormatException => 0
        }
        diffMap += (attributeAndValue(0) -> (current + attributeAndValue(1).toInt))
      }else if(anded.split("-").length == 2){
        val attributeAndValue = anded.split("-")
        val current = try {(xml \ "state" \ attributeAndValue(0)).text.toInt } catch {
          case _: NumberFormatException => 0
        }
        diffMap += (attributeAndValue(0) -> (current - attributeAndValue(1).toInt))
      }
    }
    diffMap
  }

  def injectValues(description: String, xml: scala.xml.Elem): String = {
    (for((piece,index) <- description.split("@").zipWithIndex) yield if (index % 2 == 0) piece else (xml \ "state" \ piece).text) mkString ""
  }
}