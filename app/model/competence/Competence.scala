package model.competence

import play.api.db.slick.Config.driver.simple._

trait CompetenceLikeTable[T] {
  this: Table[T] =>
  def id = column[Long]("id", O.PrimaryKey)
  def code = column[String]("code", O.NotNull)
  def description = column[String]("description", O.NotNull)
}

trait CompetenceLike {
  def id: Long
  def code: String
  def description: String
}

case class Competence(id: Long, code: String, description: String, shortDescription: String) extends CompetenceLike

class CompetenceTable(tag: Tag) extends Table[Competence](tag, "competence") with CompetenceLikeTable[Competence] {
  def shortDescription = column[String]("short_description", O.NotNull)
  
  override def * = (id, code, description, shortDescription) <> (Competence.tupled, Competence.unapply _)
}

object CompetenceTable {
  val competence: TableQuery[CompetenceTable] = TableQuery[CompetenceTable]
}

case class Element(id: Long, code: String, description: String, competenceId: Long) extends CompetenceLike

class ElementTable (tag: Tag) extends Table[Element](tag, "element") with CompetenceLikeTable[Element] {
  def competenceId = column[Long]("competence_id")
  def competence = foreignKey("competence_fk", competenceId, CompetenceTable.competence)(_.id)

  override def * = (id, code, description, competenceId) <> (Element.tupled, Element.unapply _)
}

object ElementTable {
  val element = TableQuery[ElementTable]
}

case class Indication(id: Long, code: String, description: String, elementId: Long)

class IndicationTable (tag: Tag) extends Table[Indication](tag, "indication") with CompetenceLikeTable[Indication] {
  def elementId = column[Long]("element_id")
  def element = foreignKey("element_fk", elementId, ElementTable.element)(_.id)

  override def * = (id, code, description, elementId) <> (Indication.tupled, Indication.unapply _)
}

object IndicationTable {
  val indication = TableQuery[IndicationTable]
}
