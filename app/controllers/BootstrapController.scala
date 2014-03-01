package controllers

import play.api.mvc.{Controller, Action}
import model.dataloader.FileDataLoader
import play.api.db.slick.Config.driver.simple._
import model.competence._
import model.nurse.{Nurse, NurseTable}
import model.evaluation.{EvaluatedElement, Evaluation, EvaluatedElementTable, EvaluationTable}

/**
 * Temporary resource to create the table and load the data to the in-memory database
 */
object BootstrapController extends Controller {

  def bootstrap() = Action {
    import model.dataloader.FileDataLoader._

    Database.forURL("jdbc:h2:mem:play", driver = "org.h2.Driver", user ="sa", password = "") withSession {
      implicit session =>
        (CompetenceTable.competence.ddl ++ ElementTable.element.ddl ++ IndicationTable.indication.ddl).create

        val competences = FileDataLoader.loadFromFile[Competence]("input/competence/competence.txt")
        competences.foreach(CompetenceTable.competence += _)

        val elements = FileDataLoader.loadFromFile[Element]("input/competence/element.txt")
        elements.foreach(ElementTable.element += _)

        val indications = FileDataLoader.loadFromFile[Indication]("input/competence/indication.txt")
        indications.foreach(IndicationTable.indication += _)

        NurseTable.nurse.ddl.create

        val nurses = FileDataLoader.loadFromFile[Nurse]("input/nurse/nurse.txt")
        nurses.foreach(NurseTable.nurse += _)

        (EvaluationTable.evaluation.ddl ++ EvaluatedElementTable.evaluatedElement.ddl).create

        val evaluations = FileDataLoader.loadFromFile[Evaluation]("input/evaluation/evaluation.txt")
        evaluations.foreach(EvaluationTable.evaluation += _)

        val evaluatedElements = FileDataLoader.loadFromFile[EvaluatedElement]("input/evaluation/evaluated_elements.txt")
        evaluatedElements.foreach(EvaluatedElementTable.evaluatedElement += _)
    }


    Ok("Bootstrapped!")
  }

}
