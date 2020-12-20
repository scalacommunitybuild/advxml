package advxml.core.transform

import advxml.core.transform.XmlContentZoomTest.ContractFuncs
import advxml.core.transform.XmlZoom.root
import advxml.testUtils.{ContractTests, FeatureSpecContract}
import org.scalatest.featurespec.AnyFeatureSpec

import scala.util.{Success, Try}
import scala.xml.{Elem, NodeSeq}

class XmlContentZoomTest extends AnyFeatureSpec with FeatureSpecContract {

  // format: off
  XmlContentZoomTest.Contract[Try](
    "Mandatory",
    {
      ContractFuncs(
        //attr
        attrFromNs            = XmlContentZoom.attr[Try],
        attrFromM             = XmlContentZoom.attrM[Try],
        attrFromUnbindedZoom  = (zoom, ns, key) => XmlContentZoom.attr[Try](zoom.run[Try](ns).get, key),
        attrFromBindedZoom    = (zoom, key) => XmlContentZoom.attr[Try](zoom.run[Try].get, key),
        //text
        textFromNs            = XmlContentZoom.text[Try],
        textFromM             = XmlContentZoom.textM[Try],
        textFromUnbindedZoom  = (zoom, ns) => XmlContentZoom.text[Try](zoom.run[Try](ns).get),
        textFromBindedZoom    = zoom => XmlContentZoom.text[Try](zoom.run[Try].get),
      )
    }
  )(XmlContentZoomTest.TryExtractor).runAll()
  // format: on

  // format: off
  XmlContentZoomTest.Contract[Option](
    "Optional",
    {
      ContractFuncs(
        //attr
        attrFromNs            = XmlContentZoom.attr[Option],
        attrFromM             = XmlContentZoom.attrM[Option],
        attrFromUnbindedZoom  = (zoom, ns, key) => XmlContentZoom.attr[Option](zoom.run[Option](ns).get, key),
        attrFromBindedZoom    = (zoom, key) => XmlContentZoom.attr[Option](zoom.run[Option].get, key),
        //text
        textFromNs            = XmlContentZoom.text[Option],
        textFromM             = XmlContentZoom.textM[Option],
        textFromUnbindedZoom  = (zoom, ns) => XmlContentZoom.text[Option](zoom.run[Option](ns).get),
        textFromBindedZoom    = zoom => XmlContentZoom.text[Option](zoom.run[Option].get),
      )
    }
  )(XmlContentZoomTest.OptionExtractor).runAll()
  // format: on
}

object XmlContentZoomTest {

  trait Extractor[F[_]] {
    def pure[T](t: T): F[T]
    def extract[T](fa: F[T]): T
    def isFailure[T](fa: F[T]): Boolean
  }

  case object TryExtractor extends Extractor[Try] {
    override def pure[T](t: T): Try[T] = Success(t)
    override def extract[T](fa: Try[T]): T = fa.get
    override def isFailure[T](fa: Try[T]): Boolean = fa.isFailure
  }

  case object OptionExtractor extends Extractor[Option] {
    override def pure[T](t: T): Option[T] = Some(t)
    override def extract[T](fa: Option[T]): T = fa.get
    override def isFailure[T](fa: Option[T]): Boolean = fa.isEmpty
  }

  case class ContractFuncs[F[_]](
    //attr
    attrFromNs: (NodeSeq, String) => F[String],
    attrFromM: (F[NodeSeq], String) => F[String],
    attrFromUnbindedZoom: (XmlZoom, NodeSeq, String) => F[String],
    attrFromBindedZoom: (BindedXmlZoom, String) => F[String],
    //text
    textFromNs: NodeSeq => F[String],
    textFromM: F[NodeSeq] => F[String],
    textFromUnbindedZoom: (XmlZoom, NodeSeq) => F[String],
    textFromBindedZoom: BindedXmlZoom => F[String]
  )

  case class Contract[F[_]](subDesc: String, f: ContractFuncs[F])(ex: Extractor[F])
      extends ContractTests("XmlContentZoom", subDesc) {

    test("attribute from NodeSeq") {
      val elem: Elem = <foo value="1"></foo>

      assert(ex.extract(f.attrFromNs(elem, "value")) == "1")
      assert(ex.isFailure(f.attrFromNs(elem, "rar")))
    }

    test("attribute from F[NodeSeq]") {
      val elem: F[NodeSeq] = ex.pure(<foo value="1"></foo>)

      assert(ex.extract(f.attrFromM(elem, "value")) == "1")
      assert(ex.isFailure(f.attrFromM(elem, "rar")))
    }

    test("attribute from (Unbinded)XmlZoom") {
      val elem: NodeSeq = <foo value="1"></foo>

      assert(ex.extract(f.attrFromUnbindedZoom(root, elem, "value")) == "1")
      assert(ex.isFailure(f.attrFromUnbindedZoom(root, elem, "rar")))
    }

    test("attribute from BindedXmlZoom") {
      val elem: BindedXmlZoom = root(<foo value="1"></foo>)

      assert(ex.extract(f.attrFromBindedZoom(elem, "value")) == "1")
      assert(ex.isFailure(f.attrFromBindedZoom(elem, "rar")))
    }

    test("text from NodeSeq") {
      val elemWithText: Elem = <foo>TEST</foo>
      val elemWithoutText = <foo></foo>

      assert(ex.extract(f.textFromNs(elemWithText)) == "TEST")
      assert(ex.isFailure(f.textFromNs(elemWithoutText)))
    }

    test("text from F[NodeSeq]") {
      val elemWithText: F[NodeSeq] = ex.pure(<foo>TEST</foo>)
      val elemWithoutText: F[NodeSeq] = ex.pure(<foo></foo>)

      assert(ex.extract(f.textFromM(elemWithText)) == "TEST")
      assert(ex.isFailure(f.textFromM(elemWithoutText)))
    }

    test("text from (Unbinded)XmlZoom") {
      val elemWithText: Elem = <foo>TEST</foo>
      val elemWithoutText = <foo></foo>

      assert(ex.extract(f.textFromUnbindedZoom(root, elemWithText)) == "TEST")
      assert(ex.isFailure(f.textFromUnbindedZoom(root, elemWithoutText)))
    }

    test("text from BindedXmlZoom") {
      val elemWithText: BindedXmlZoom = root(<foo>TEST</foo>)
      val elemWithoutText: BindedXmlZoom = root(<foo></foo>)

      assert(ex.extract(f.textFromBindedZoom(elemWithText)) == "TEST")
      assert(ex.isFailure(f.textFromBindedZoom(elemWithoutText)))
    }
  }
}
