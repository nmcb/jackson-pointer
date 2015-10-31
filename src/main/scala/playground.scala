import com.fasterxml.jackson.databind.ObjectMapper

object playground extends App{

  val mapper = new ObjectMapper().setNodeFactory(new NamedJsonNodeFactory)
  val root = mapper.readTree(
    """
      | {
      |   "foo": {
      |     "bar": [1, 2, 3, 4, 5],
      |     "qux": {
      |       "ducks": ["kwik", "kwek", "kwak"]
      |     }
      |   }
      | }
    """.stripMargin)

  println(root)
}
