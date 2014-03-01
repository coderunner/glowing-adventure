package model.dataloader

import java.io.{FileReader, BufferedReader}
import scala.annotation.tailrec
import model.competence.{Indication, Element, Competence}
import model.nurse.{WorkShift, WorkUnit, Nurse}
import model.evaluation.{EvaluatedElement, Stage, Evaluation}

/**
 * Functions to load data from files.
 */
object FileDataLoader {

  def loadFromFile[T](filename: String)(implicit mapper: Array[String] => T): List[T] = {
    val reader = new BufferedReader(new FileReader(filename))

    @tailrec
    def loadData(reader: BufferedReader, acc: List[T]): List[T] = {
      val line = Option(reader.readLine())
      line match {
        case None => acc
        case Some(l) if l.startsWith("#") => loadData(reader, acc)
        case Some(l) => loadData(reader, mapper(l.split(";")) :: acc)
      }
    }

    loadData(reader, Nil).reverse
  }

  implicit def competenceMapper(values: Array[String]): Competence = {
    val id = values(0).toLong
    Competence(id, values(1), values(2), values(3))
  }

  implicit def elementMapper(values: Array[String]): Element = {
    val id = values(0).toLong
    Element(id, values(1), values(2), values(3).toLong)
  }

  implicit def indicationMapper(values: Array[String]): Indication = {
    Indication(values(0).toLong, values(1), values(2), values(3).toLong)
  }

  implicit def nurseMapper(values: Array[String]): Nurse = {
    Nurse(values(0).toLong, values(1), WorkUnit.withName(values(2)).id, WorkShift.withName(values(3)).id)
  }

  implicit def evaluationMapper(values: Array[String]): Evaluation = {
    Evaluation(values(0).toLong, values(1).toLong, values(2).toInt)
  }

  implicit def evaluatesElementMapper(values: Array[String]): EvaluatedElement = {
    EvaluatedElement(values(0).toLong, values(1).toLong, Stage.withName(values(2)).id)
  }

}
