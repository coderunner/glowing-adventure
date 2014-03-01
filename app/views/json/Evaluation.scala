package views.json

import model.evaluation.Stage.Stage
import play.api.libs.json.{Json, JsString, JsValue, Writes}

/**
 * Json classes and writers for evaluations.
 */

case class JEvaluation(id: Long, nurseId: JNurse, evaluatedElements: List[JEvaluatedElements])
case class JEvaluatedElements(elementId: Long, stage: Stage)

object JEvaluation {
  implicit val stageWriter = new Writes[Stage] {
    override def writes(o: Stage): JsValue = JsString(o.toString)
  }
  implicit val evaluatedElementWriter = Json.writes[JEvaluatedElements]
  implicit val evaluationWriter = Json.writes[JEvaluation]
}