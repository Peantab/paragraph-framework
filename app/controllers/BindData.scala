package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

import scala.xml.Node
import scala.xml.transform.{RewriteRule, RuleTransformer}

/**
  * Created by Paweł Taborowski on 24.06.17.
  */
class BindData @Inject() (ws: WSClient) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  def bind() = Action.async {
    implicit request =>
      val xmlAndData: XMLAndData = xmlForm.bindFromRequest().get
      val code = scala.xml.XML.loadString(xmlAndData.xml)
      val pageId = (code \ "state" \ "page-id").text
      val selections = code \ "selection"
      val selectionNumbers = selections.map(i => i \ "@page-id")

      val pairs = xmlAndData.map.split("=|:").grouped(2)
      val map = pairs.map {case Array(k, v) => k -> v}.toMap

      var codeNode: Node = code
      for ((k,v) <- map){
        val rule = new RuleTransformer(new ChangeState(k,v))
        codeNode = rule.transform(codeNode)(0)
      }

      implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
      ws.url("http://" + request.host + "/navigate/").post(Map("xml" -> Seq(codeNode.toString()))).map { response =>
        Ok(Html(response.body))
      }
  }

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText,
      "map" -> nonEmptyText
    )(XMLAndData.apply)(XMLAndData.unapply)
  )
}

case class XMLAndData(xml: String, map: String)

class ChangeState(key: String, value: String) extends RewriteRule{

  def textElem(name: String, text: String) =  scala.xml.Elem(null, name, scala.xml.Null, scala.xml.TopScope, false, scala.xml.Text(text))

  override def transform(n: Node): Seq[Node] = n match {
    case e: scala.xml.Elem if e.label == "state" =>
      var stateElem = for (n <- e.child if n.label != key) yield n
//      stateElem = stateElem.filter(_.label != key)
      stateElem = textElem(key, value) ++ stateElem
      new scala.xml.Elem(e.prefix, "state", e.attributes, e.scope, e.minimizeEmpty, (stateElem).toSeq:_*)
    case x => x
  }
}