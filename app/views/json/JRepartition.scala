package views.json

import play.api.libs.json.Json
import model.evaluation.Stage
import model.evaluation.Stage.Stage

/**
 * Json classes and writers for repartition analysis.
 */
case class JRepartition(d: JStepRepartition, a: JStepRepartition, rc: JStepRepartition, e: JStepRepartition)

case class JStepRepartition(count: Int)

object JRepartition {
  implicit val stepRepartitionWriter = Json.writes[JStepRepartition]
  implicit val repartitionWriter = Json.writes[JRepartition]

  def fromMap(repartition: Map[Stage, Int]) = {
    JRepartition(
      JStepRepartition(repartition.get(Stage.D).getOrElse(0)),
      JStepRepartition(repartition.get(Stage.A).getOrElse(0)),
      JStepRepartition(repartition.get(Stage.R).getOrElse(0)),
      JStepRepartition(repartition.get(Stage.EC).getOrElse(0)))

  }

}
