package util

import scala.slick.lifted.AbstractTable
import play.api.Play

/**
 * Helper functions to interact with the database.
 */
object DatabaseHelper {
  import play.api.db.slick.Config.driver.simple._
  val driver = Play.current.configuration.getString("db.default.driver").get
  val url = Play.current.configuration.getString("db.default.url").get
  val username = Play.current.configuration.getString("db.default.user").get
  val password = Play.current.configuration.getString("db.default.password").get

  def list[E<: AbstractTable[_]](tableQuery: TableQuery[E]): List[E#TableElementType] = {
    Database.forURL(url = url, driver = driver, user = username, password = password) withSession {
      implicit session =>
        tableQuery.list()
    }
  }

  def withDatabaseSession[T](block: (Session) => T): T = {
    Database.forURL(url = url, driver = driver, user = username, password = password) withSession {
      implicit session =>
        block(session)
    }
  }

  def query[T](block: (Session) => T) = {
    Database.forURL(url = url, driver = driver, user = username, password = password) withSession {
      implicit session =>
        block(session)
    }
  }

}
