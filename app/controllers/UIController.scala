package controllers

import play.api.mvc.{Action, Controller}
import model.nurse.{WorkShift, WorkUnit}
import play.api.libs.json.Json
import views.json.JNurse._

/**
 * Controller serving static data to help UI.
 */
object UIController extends Controller {

  def getWorkUnits = Action {
   Ok(Json.toJson(WorkUnit.values.toList))
  }

  def getWorkShifts = Action {
    Ok(Json.toJson(WorkShift.values.toList))
  }

}
