package controllers

import play.api.mvc.{Action, Controller}

/**
 * Root controller, redirecting to the homepage..
 */
object MainController extends Controller {

  def index = Action {
    Redirect("/assets/index.html")
  }



}
