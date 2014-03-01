package model.evaluation

import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.Tag
import model.nurse.{WorkShift, WorkUnit, NurseTable}
import model.competence.ElementTable
import util.DatabaseHelper
import views.json.{JEvaluation, JNurse, JEvaluatedElements}

/**
 * Database model for Evaluations
 */
case class Evaluation(id: Long, nurseId: Long, year: Int)

class EvaluationTable(tag: Tag) extends Table[Evaluation](tag, "evaluation") {
  def id = column[Long]("id", O.PrimaryKey)
  def nurseId = column[Long]("nurse_id", O.NotNull)
  def year = column[Int]("year", O.NotNull)

  foreignKey("FK_NURSE", nurseId, NurseTable.nurse)(_.id)

  def * = (id, nurseId, year) <> (Evaluation.tupled, Evaluation.unapply _)
}

object EvaluationTable {
  val evaluation = TableQuery[EvaluationTable]
}

case class EvaluatedElement(evaluationId: Long, elementId: Long, evaluation: Int)

object Stage extends Enumeration {
  type Stage = Value

  val D, A, R, EC = Value
}

class EvaluatedElementTable(tag: Tag) extends Table[EvaluatedElement](tag, "evaluated_element") {
  def evaluationId = column[Long]("evaluation_id", O.NotNull)
  def elementId = column[Long]("element_id", O.NotNull)
  def evaluation = column[Int]("evaluation", O.NotNull)

  foreignKey("FK_EVAL", evaluationId, EvaluationTable.evaluation)(_.id)
  foreignKey("FK_ELEM", elementId, ElementTable.element)(_.id)
  primaryKey("PK_EVAL_ELEM", (evaluationId, elementId))

  def * = (evaluationId, elementId, evaluation) <> (EvaluatedElement.tupled, EvaluatedElement.unapply _)
}

object EvaluatedElementTable {
  val evaluatedElement = TableQuery[EvaluatedElementTable]
}
