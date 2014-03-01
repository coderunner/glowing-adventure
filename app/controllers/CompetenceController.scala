package controllers

import play.api.mvc._
import play.api.libs.json.Json
import model.competence.{IndicationTable, ElementTable, CompetenceTable}
import views.json.{JIndication, JElement, JCompetence}
import play.api.db.slick.Config.driver.simple._
import play.api.cache.Cache
import play.api.Play.current
import util.DatabaseHelper

/**
 * Controller to server competence informations.
 */
object CompetenceController extends Controller {

  def index = Action {
    val competences = Cache.get("competence.all").getOrElse(loadCompetences()).asInstanceOf[List[JCompetence]]
    Ok(Json.toJson(competences))
  }

  def get(id: Long) = Action {
    val competence = Cache.get("competence." + id).getOrElse(loadCompetences().find(_.id == id).get).asInstanceOf[JCompetence]
    Ok(Json.toJson(competence))
  }

  private def loadCompetences(): List[JCompetence] = {
    val competences = DatabaseHelper.query {
      implicit session =>
        val elementIdToIndications = IndicationTable.indication.list().groupBy(_.elementId)
        val competenceIdToElements = ElementTable.element.list().groupBy(_.competenceId)
        val competences = CompetenceTable.competence.list()

        competences.map { c =>
          val jelements = competenceIdToElements(c.id).map { e =>
            val jindications = elementIdToIndications(e.id).map { i =>
              JIndication(i.id, i.code, i.description)
            }
            JElement(e.id, e.code, e.description, jindications)
          }
          JCompetence(c.id, c.code, c.description, c.shortDescription, jelements)
        }

    }
    competences.foreach(c => Cache.set("competence." + c.id, c))
    Cache.set("competence.all", competences)
    competences
  }

}
