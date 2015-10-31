import java.lang
import java.lang.{Double, Byte, Short, Long}
import java.math.{BigDecimal, BigInteger}

import com.fasterxml.jackson.core.{JsonGenerator, JsonToken, JsonPointer}
import com.fasterxml.jackson.databind.{JsonNode, SerializerProvider}
import com.fasterxml.jackson.databind.node._

trait NamedNode {
  var name: JsonPointer = ???
}

object NamedNode {
//  def loop(json: ObjectNode, acc: => JsonPointer): Map[JsonPointer, ValueNode] = {
//    json.arrayOrObject[Map[JsonPointer, Json]](
//      or = Map.empty,
//      jsonArray = array => {
//        array.zipWithIndex.map{ case (j, i) =>
//          loop(j, acc :+ PointerNode.Index(i))
//        }.reduceOption(_ ++ _).getOrElse(Map.empty)
//      },
//      jsonObject = obj => {
//        obj.toMap.map{ case (k, v) =>
//          loop(v, acc :+ PointerNode.Node(k))
//        }.reduceOption(_ ++ _).getOrElse(Map.empty)
//      }
//    ) ++ Map(acc -> json)
//  }
}

class NamedObjectNode(nc: NamedJsonNodeFactory) extends ObjectNode(nc) with NamedNode
class NamedArrayNode(nc: NamedJsonNodeFactory) extends ArrayNode(nc) with NamedNode

class NamedJsonNodeFactory extends JsonNodeFactory {
  override def objectNode(): ObjectNode = new NamedObjectNode(this)
  override def arrayNode(): ArrayNode = new NamedArrayNode(this)
}
