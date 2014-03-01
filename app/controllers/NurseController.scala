package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import model.nurse.{WorkUnit, WorkShift, NurseTable}
import views.json.JNurse
import play.api.db.slick.Config.driver.simple._
import util.DatabaseHelper

/**
 * Controller serving nurse information.
 */
object NurseController extends Controller {

  def index = Action {
    Ok(Json.toJson(DatabaseHelper.list(NurseTable.nurse).map { n =>
      JNurse(n.id, n.name, WorkUnit(n.workUnit), WorkShift(n.workShift))
    }))
  }

  def get(id: Long) = Action {
    val nurse = DatabaseHelper.withDatabaseSession { implicit session =>
      NurseTable.nurse.filter(_.id === id).firstOption
    }

    Ok(Json.toJson(nurse.map(n => JNurse(n.id, n.name, WorkUnit(n.workUnit), WorkShift(n.workShift)))))
  }

}
