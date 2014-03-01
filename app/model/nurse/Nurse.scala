package model.nurse

import play.api.db.slick.Config.driver.simple._

/**
 * Database model for nurses.
 */
case class Nurse(id: Long, name: String, workUnit: Int, workShift: Int)

object WorkUnit extends Enumeration {
  type WorkUnit = Value

  val EMERGENCY,
  INTENSIVE_CARE,
  HEMATO_ONCO,
  OBSTETRIC,
  UNIT_MOTHER_CHILD,
  NEO_NATALOGY,
  SURGERY,
  OP_BLOCK,
  INFECTIONS_DISEASE = Value
}

object WorkShift extends Enumeration {
  type WorkShift = Value

  val DAY, EVENING, NIGHT = Value
}

class NurseTable(tag: Tag) extends Table[Nurse](tag, "nurse") {
  def id = column[Long]("id", O.PrimaryKey)
  def name = column[String]("name", O.NotNull)
  def workUnit = column[Int]("work_unit")
  def workShift = column[Int]("work_shift")

  override def * = (id, name, workUnit, workShift) <> (Nurse.tupled, Nurse.unapply _)
}

object NurseTable {
  def nurse = TableQuery[NurseTable]
}
