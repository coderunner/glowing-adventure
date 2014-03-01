package views.json

import model.nurse.WorkShift.WorkShift
import model.nurse.WorkUnit.WorkUnit
import play.api.libs.json.{JsString, JsValue, Writes, Json}

/**
 * Json classes and writers for nurse.
 */
case class JNurse(id: Long, name: String, workUnit: WorkUnit, workShift: WorkShift)

object JNurse {
  implicit val workUnitWriter = new Writes[WorkUnit] {
    override def writes(o: WorkUnit): JsValue = JsString(o.toString)
  }
  implicit val workShiftWriter = new Writes[WorkShift] {
    override def writes(o: WorkShift): JsValue = JsString(o.toString)
  }
  implicit val writer = Json.writes[JNurse]
}
