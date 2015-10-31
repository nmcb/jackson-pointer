import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest._

class NamedNodeSpec extends FunSpec with MustMatchers {
  val mapper = new ObjectMapper().setNodeFactory(new NamedJsonNodeFactory)
  describe("NamedObject") {

    val root = mapper.readTree(
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

    it("must be of the right type") {
      root.isInstanceOf[NamedObjectNode] mustBe true
      root.get("object").isInstanceOf[NamedObjectNode] mustBe true
      root.get("object").get("nested-array").isInstanceOf[NamedArrayNode] mustBe true
      root.get("object").get("nested-object").isInstanceOf[NamedObjectNode] mustBe true
      root.get("array").get(0).isInstanceOf[NamedObjectNode] mustBe true
    }

  }
}
