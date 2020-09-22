package advxml.core.transform

import advxml.core.transform.XmlPatch.NodeSeqPatchMap

import scala.xml.NodeSeq

class XmlPatch private (val original: NodeSeq, val updated: NodeSeq) {

  private[transform] def zipWithUpdated: NodeSeqPatchMap = {
    import cats.implicits._
    original.toList.padZip(updated.toList).toMap
  }
}

object XmlPatch {

  type NodeSeqPatchMap = Map[Option[NodeSeq], Option[NodeSeq]]

  private[transform] def id(original: NodeSeq): XmlPatch = const(original, original)

  private[transform] def const(original: NodeSeq, updated: NodeSeq): XmlPatch =
    apply(original, Function.const(updated))

  private[transform] def apply(original: NodeSeq, f: NodeSeq => NodeSeq): XmlPatch =
    new XmlPatch(original, f(original))
}