package controllers

import play.api.mvc.{Action, Controller}
import util.DatabaseHelper
import model.evaluation._
import model.nurse.{Nurse, WorkShift, WorkUnit, NurseTable}
import play.api.db.slick.Config.driver.simple._
import views.json.JRepartition
import model.evaluation.Stage.Stage
import model.evaluation.EvaluatedElement
import scala.Some
import play.api.libs.json.Json
import views.json.JRepartition._
import model.competence.ElementTable

/**
 * Controller serving repartition data.
 */
object RepartitionController extends Controller {

  def get(workUnit: Option[String], workShift: Option[String]) = Action {
    Ok(Json.toJson(createRepartition(workUnit, workShift, None)))
  }

  def getByElementId(elementId: Long, workUnit: Option[String], workShift: Option[String]) = Action {
    Ok(Json.toJson(createRepartition(workUnit, workShift, Some(Set(elementId)))))
  }

  def getByCompetenceId(competenceId: Long, workUnit: Option[String], workShift: Option[String]) = Action {
    val elementIds = DatabaseHelper.query {
      implicit session =>
        (for {
          e <- ElementTable.element
          if e.competenceId === competenceId
        } yield e.id).buildColl[Set]
    }
    Ok(Json.toJson(createRepartition(workUnit, workShift, Some(elementIds))))
  }

  private def createRepartition(workUnit: Option[String], workShift: Option[String], elementIdFilter: Option[Set[Long]]): JRepartition = {
    val ev = getFilteredEvaluations(workUnit, workShift).map {
      e =>
        computeAverageStageForEvaluatedElements(e._2, elementIdFilter)
    }
    val repartition = ev.groupBy(_.id).map(e => Stage(e._1) -> e._2.size)
    JRepartition.fromMap(repartition)
  }

  private def computeAverageStageForEvaluatedElements(evaluatedElements: List[EvaluationElements], optFilter: Option[Set[Long]]): Stage = {
    val filteredElements = optFilter match {
      case None => evaluatedElements
      case Some(ids) => evaluatedElements.filter(e => ids.contains(e.elementId))
    }
    val average = math.round(filteredElements.map(_.evaluation).sum / filteredElements.length.toDouble).toInt
    Stage(average)
  }

  private def getFilteredEvaluations(workUnit: Option[String], workShift: Option[String]): Map[Long, List[EvaluationElements]] = {
    DatabaseHelper.query {
      implicit session =>
        val joined = for {
          e <- EvaluationTable.evaluation
          n <- NurseTable.nurse
          if e.nurseId === n.id
          ee <- EvaluatedElementTable.evaluatedElement
          if e.id === ee.evaluationId
        } yield (e, n, ee)
        val filtered = addFilterByWorkShift(addFilterByWorkUnit(joined, workUnit), workShift)
        val projected = filtered.map(ee => (ee._1.id, ee._3.elementId, ee._3.evaluation))
        projected.list().map(e => EvaluationElements(e._1, e._2, e._3)).groupBy(_.evaluationId)
    }
  }

  private def addFilterByWorkUnit(query: Query[(EvaluationTable, NurseTable, EvaluatedElementTable), (Evaluation, Nurse, EvaluatedElement)],
                                  workUnit: Option[String]): Query[(EvaluationTable, NurseTable, EvaluatedElementTable), (Evaluation, Nurse, EvaluatedElement)] = {
    val optWorkUnitFilter = workUnit.map(WorkUnit.withName(_).id)
    optWorkUnitFilter match {
      case None => query
      case Some(workUnitId) => for {e <- query if e._2.workUnit === workUnitId} yield e
    }
  }

  private def addFilterByWorkShift(query: Query[(EvaluationTable, NurseTable, EvaluatedElementTable), (Evaluation, Nurse, EvaluatedElement)],
                                   workShift: Option[String]): Query[(EvaluationTable, NurseTable, EvaluatedElementTable), (Evaluation, Nurse, EvaluatedElement)] = {
    val optWorkShiftFilter = workShift.map(WorkShift.withName(_).id)
    optWorkShiftFilter match {
      case None => query
      case Some(workShiftId) => for {e <- query if e._2.workShift === workShiftId} yield e
    }
  }

  case class EvaluationElements(evaluationId: Long, elementId: Long, evaluation: Int)

}
