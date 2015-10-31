package naming

import com.fasterxml.jackson.databind.JsonNode

import scala.collection.SortedMap

sealed abstract class PointerNode {
  def asString: String
}

object PointerNode {

  final case class Node(private val raw: String) extends PointerNode {
    override def asString = raw.replace("~", "~0").replace("/", "~1")
  }
  final case class Index(i: Long) extends PointerNode {
    assert(i >= 0, "index must be positive")
    override def asString = i.toString
  }
  final case class JsonPointer(values: List[PointerNode]) {
    def |+|(that: JsonPointer): JsonPointer = JsonPointer(values ++ that.values)
    def :+(node: PointerNode): JsonPointer = JsonPointer(values :+ node)
    def +:(node: PointerNode): JsonPointer = JsonPointer(node +: values)
    override def toString: String = values.map(_.asString).mkString("/", "/", "")
  }

  implicit object PointerNodeOrdering extends Ordering[PointerNode] {
    override def compare(x: PointerNode, y: PointerNode): Int = (x, y) match {
      case (Index(_), Node(_))  => -1
      case (Node(_), Index(_))  => 1
      case (Index(a), Index(b)) => a compare b
      case (Node(a), Node(b))   => a compare b
    }
  }

  object JsonPointer {
    val root: JsonPointer = JsonPointer(List.empty)

    implicit object JsonPointerOrdering extends Ordering[JsonPointer] {
      override def compare(x: JsonPointer, y: JsonPointer): Int = {
        def recurse(xs: List[PointerNode], ys: List[PointerNode]): Int = (xs, ys) match {
          case (Nil, Nil)           => 0
          case (Nil, _)             => -1
          case (_, Nil)             => 1
          case (h1 :: t1, h2 :: t2) => {
            val c = PointerNodeOrdering.compare(h1, h2)
            if (c != 0) c else recurse(t1, t2)
          }
        }
        recurse(x.values, y.values)
      }
    }

    def toSortedMap(json: JsonNode): SortedMap[JsonPointer, JsonNode] = loop(json, root)

    private def loop(json: JsonNode, acc: JsonPointer): SortedMap[JsonPointer, JsonNode] = {
      import scala.collection.JavaConversions._
      if (json.isObject) {
        SortedMap(acc -> json) ++ json.fields.map { entry =>
          val (k, v) = (entry.getKey, entry.getValue)
          loop(v, acc :+ PointerNode.Node(k))
        }.reduce(_ ++ _)
      }
      else if (json.isArray) {
        SortedMap(acc -> json) ++ json.elements.zipWithIndex.map { case (v, i) =>
          loop(v, acc :+ PointerNode.Index(i))
        }.reduce(_ ++ _)
      }
      else {
        SortedMap(acc -> json)
      }
    }
  }
}