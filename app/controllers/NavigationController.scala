package controllers

import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.xml.Elem

/**
  * Created by Paweł Taborowski on 23.06.17.
  */
class NavigationController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport{
  def navigate() = Action
  {
    implicit request =>
      val xml : UserXML = xmlForm.bindFromRequest().get
      val code = scala.xml.XML.loadString(xml.xml)
      val pageId = (code \ "state" \ "page-id").text
      val selections = code \ "selection"
      val selectionNumbers = selections.map(i => i \ "@page-id")

      if (/*selectionNumbers.contains(pageId)*/ true){
        Redirect(routes.SelectionController.index(xml.xml))
      }else {
        Ok(views.html.main("ALA")(content = views.html.welcome("ERROR", "ma", selectionNumbers.toString(), pageId.toString)))
      }
  }

  val xmlForm = Form(
    mapping(
      "xml" -> nonEmptyText
    )(UserXML.apply)(UserXML.unapply)
  )
}

