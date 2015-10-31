import com.fasterxml.jackson.databind.ObjectMapper
import naming.PointerNode.JsonPointer.JsonPointerOrdering
import naming.PointerNode.{Index, JsonPointer, Node, PointerNodeOrdering}
import org.scalatest._

class JsonPointerSpec extends FunSpec with MustMatchers {
  describe("JsonPointer") {

    val root = new ObjectMapper().readTree(
      """
        | {
        |   "object": {
        |     "nested-array": [1],
        |     "nested-object": {"a": 1}
        |   },
        |   "array": [{"a": 1}, ["a"]],
        |   "bool": true,
        |   "null": null,
        |   "number": 123.456
        | }
      """.stripMargin)

    it("must map all pointers") {
      val root = new ObjectMapper().readTree(
        """
          | {
          |   "object": {
          |     "nested-array": [1],
          |     "nested-object": {"a": 1}
          |   },
          |   "array": [{"a": 1}, ["a"]],
          |   "bool": true,
          |   "null": null,
          |   "number": 123.456
          | }
        """.stripMargin)

      JsonPointer.toSortedMap(root).mkString("\n") mustBe {
        """
          |/ -> {"object":{"nested-array":[1],"nested-object":{"a":1}},"array":[{"a":1},["a"]],"bool":true,"null":null,"number":123.456}
          |/array -> [{"a":1},["a"]]
          |/array/0 -> {"a":1}
          |/array/0/a -> 1
          |/array/1 -> ["a"]
          |/array/1/0 -> "a"
          |/bool -> true
          |/null -> null
          |/number -> 123.456
          |/object -> {"nested-array":[1],"nested-object":{"a":1}}
          |/object/nested-array -> [1]
          |/object/nested-array/0 -> 1
          |/object/nested-object -> {"a":1}
          |/object/nested-object/a -> 1
        """.stripMargin.trim
      }
    }

    it("must correctly order pointer nodes") {
      PointerNodeOrdering.compare(Index(9), Node("a")) must be < 0
      PointerNodeOrdering.compare(Node("z"), Index(0)) must be > 0

      PointerNodeOrdering.compare(Node("a"), Node("z")) must be < 0
      PointerNodeOrdering.compare(Node("z"), Node("a")) must be > 0
      PointerNodeOrdering.compare(Node("a"), Node("a")) mustBe 0

      PointerNodeOrdering.compare(Index(0), Index(9)) must be < 0
      PointerNodeOrdering.compare(Index(9), Index(0)) must be > 0
      PointerNodeOrdering.compare(Index(0), Index(0)) mustBe 0
    }

    it("must correctly order json pointers") {
      val aa = JsonPointer(List(Node("a"), Node("a")))
      JsonPointerOrdering.compare(aa, aa) mustBe 0

      val aaa = JsonPointer(List(Node("a"), Node("a"), Node("a")))
      JsonPointerOrdering.compare(aa, aaa) must be < 0
      JsonPointerOrdering.compare(aaa, aa) must be > 0

      val aaz = JsonPointer(List(Node("a"), Node("a"), Node("z")))
      val aza = JsonPointer(List(Node("a"), Node("z"), Node("a")))
      JsonPointerOrdering.compare(aaz, aza) must be < 0
      JsonPointerOrdering.compare(aza, aaz) must be > 0

      val aa0 = JsonPointer(List(Node("a"), Node("a"), Index(0)))
      val aa1 = JsonPointer(List(Node("a"), Node("a"), Index(1)))
      JsonPointerOrdering.compare(aa0, aa1) must be < 0
      JsonPointerOrdering.compare(aa1, aa0) must be > 0
    }

  }
}
