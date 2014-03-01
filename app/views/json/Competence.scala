package views.json

import play.api.libs.json.Json


/**
 * Json classes and writers for competence.
 */

case class JCompetence(id: Long, code: String, description: String, shortDescription: String, elements: List[JElement])
case class JElement(id: Long, code: String, description: String, indications: List[JIndication])
case class JIndication(id: Long, code: String, description: String)

object JCompetence {
  implicit val indicationWriter = Json.writes[JIndication]
  implicit val elementWriter = Json.writes[JElement]
  implicit val competenceWriter = Json.writes[JCompetence]
}


