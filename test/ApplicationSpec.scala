import controllers.ParagraphController
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import controllers.ParagraphController._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Paragraph")
    }

  }

  "ParagraphController.effect" should {
    "insert values from effect script into a map" in {
      val effect = "alaMa=kota&olaMa=psa"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><elaMa>rybki</elaMa></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("alaMa" -> "kota", "olaMa" -> "psa")
    }

    "insert already existing (same) values from effect script into a map" in {
      val effect = "elaMa=rybki"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><elaMa>rybki</elaMa></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("elaMa" -> "rybki")
    }

    "increase value that already exist in an xml" in {
      val effect = "alaMaKotow@7"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>5</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("alaMaKotow" -> 12)
    }

    "increase value that does not exist in an xml (assume 0)" in {
      val effect = "alaMaKotow@7"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("alaMaKotow" -> 7)
    }

    "decrease value that already exist in an xml" in {
      val effect = "alaMaKotow-7"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>5</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("alaMaKotow" -> -2)
    }

    "decrease value that does not exist in an xml (assume 0)" in {
      val effect = "alaMaKotow-7"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("alaMaKotow" -> -7)
    }

    "be able to execute all types of operations in a one run" in {
      val effect = "alaMaKotow-7&olaMaPsow@3&elaMaRybek=mnostwo"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>1</alaMaKotow><olaMaPsow>2</olaMaPsow><elaMaRybek>99</elaMaRybek></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.effect(effect, elem) mustBe Map("alaMaKotow" -> -6, "olaMaPsow" -> 5, "elaMaRybek" -> "mnostwo")
    }
  }

  "ParagraphController.cndtn" should {
    "compare two equal Strings" in {
      val cndtn = "alaMa=kota"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMa>kota</alaMa></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe true
    }

    "detect that two Strings are not equal" in {
      val cndtn = "alaMa=kota"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMa>tygrysa</alaMa></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "detect that String in xml does not exist (so is not equal)" in {
      val cndtn = "alaMa=kota"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><olaMa>kota</olaMa></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "find out, that 34 is greater than 5" in {
      val cndtn = "alaMaKotow>5"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>34</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe true
    }

    "find out, that none being 0 is not greater than 5" in {
      val cndtn = "alaMaKotow>5"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "find out, that 4 is not greater than 5" in {
      val cndtn = "alaMaKotow>5"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>4</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "find out, that 34 is not lesser than 5" in {
      val cndtn = "alaMaKotow<5"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>34</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "find out, that none being 0 is lesser than 5" in {
      val cndtn = "alaMaKotow<5"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe true
    }

    "find out, that 4 is lesser than 5" in {
      val cndtn = "alaMaKotow<5"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>4</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe true
    }

    "find out, that 4 is not lesser than 4" in {
      val cndtn = "alaMaKotow<4"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>4</alaMaKotow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "find out that three anded values that should be true are true indeed" in {
      val cndtn = "alaMaKotow<4&olaMaPsow=10&elaMaRybek>-98"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>3</alaMaKotow><olaMaPsow>10</olaMaPsow></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe true
    }

    "find out that three anded values that should be evaluated to false are false indeed" in {
      val cndtn = "alaMaKotow<4&olaMaPsow=10&elaMaRybek>-98"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>3</alaMaKotow><olaMaPsow>10</olaMaPsow><elaMaRybek>-310</elaMaRybek></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }

    "find out that two anded values being true ored with a false value are evaluated to true" in {
      val cndtn = "alaMaKotow<4&olaMaPsow=10:elaMaRybek>-98"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>3</alaMaKotow><olaMaPsow>10</olaMaPsow><elaMaRybek>-310</elaMaRybek></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe true
    }

    "find out that two anded values being false ored with two anded values being false are evaluated to false" in {
      val cndtn = "alaMaKotow<4&elaMaRybek>-98:elaMaRybek>-98&olaMaPsow=10"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><alaMaKotow>3</alaMaKotow><olaMaPsow>10</olaMaPsow><elaMaRybek>-310</elaMaRybek></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.cndtn(cndtn, elem) mustBe false
    }
  }

  "ParagraphController.injectValues" should {
    "insert a value in it's place" in {
      val dscrptn = "Ala ma @zwierz@, a Ela ma @nieMieso@."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><zwierz>kota</zwierz><nieMieso>rybki</nieMieso></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma kota, a Ela ma rybki."
    }

    "insert a value in the beginning" in {
      val dscrptn = "@ktos@ ma kota."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><ktos>Ala</ktos></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma kota."
    }

    "insert a value in the end" in {
      val dscrptn = "Ala ma @zwierz@"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><zwierz>kota</zwierz></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma kota"
    }

    "insert only variable" in {
      val dscrptn = "@odpowiedz@"
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><odpowiedz>czterdziesci dwa</odpowiedz></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "czterdziesci dwa"
    }

    "insert a number" in {
      val dscrptn = "Odpowiedz to @odpowiedz@."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><odpowiedz>42</odpowiedz></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Odpowiedz to 42."
    }

    "find out that condition is met and print a literal" in {
      val dscrptn = "Ala ma @koty=1#zwierze@."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><koty>1</koty></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma zwierze."
    }

    "find out that condition is not met and print nothing" in {
      val dscrptn = "Ala ma @koty=1#zwierze@."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><koty>0</koty></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma ."
    }

    "find out that condition is met in ternary operator, print appropriate literal" in {
      val dscrptn = "Ala ma @koty=1#kota#koty@."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><koty>1</koty></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma kota."
    }

    "find out that condition is not met in ternary operator, print appropriate literal" in {
      val dscrptn = "Ala ma @koty=1#kota#koty@."
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gamebook><state><koty>5</koty></state></gamebook>"
      val elem = scala.xml.XML.loadString(xml)
      ParagraphController.injectValues(dscrptn, elem) mustBe "Ala ma koty."
    }
  }

}
