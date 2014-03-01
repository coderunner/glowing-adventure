package controllers

import play.api.mvc.{Action, Controller}
import model.evaluation.{Stage, EvaluatedElementTable, EvaluationTable}
import views.json._
import play.api.db.slick.Config.driver.simple._
import views.json.JEvaluatedElements
import model.nurse.{WorkShift, WorkUnit, NurseTable}
import play.api.libs.json.Json
import util.DatabaseHelper

/**
 * Controller to serve evaluations.
 */
object EvaluationController extends Controller {

  def get(id: Long) = Action {
    val ev = DatabaseHelper.query {
      implicit session =>
        val evaluation = EvaluationTable.evaluation.filter(_.id === id).firstOption
        evaluation.map {
          e =>
            val evaluatedElements = EvaluatedElementTable.evaluatedElement.filter(_.evaluationId === id).list().map {
              ee =>
                JEvaluatedElements(ee.elementId, Stage(ee.evaluation))
            }
            val nurse = NurseTable.nurse.filter(_.id === id).firstOption.map(n => JNurse(n.id, n.name, WorkUnit(n.workUnit), WorkShift(n.workShift)))
            JEvaluation(id, nurse.getOrElse(null), evaluatedElements)
        }
    }
    Ok(Json.toJson(ev))
  }


}
