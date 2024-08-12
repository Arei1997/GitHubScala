package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import service.LibraryService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

//@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, libraryService: LibraryService)(implicit ec: ExecutionContext) extends BaseController {

  def getGitHubUser(username: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    libraryService.getGitHubUser(username).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("error" -> "User not found"))
    }
  }


  def index(): Action[AnyContent] = TODO
  def create(): Action[AnyContent] = TODO
  def read(): Action[AnyContent] = TODO
  def update(): Action[AnyContent] = TODO
  def delete(): Action[AnyContent] = TODO


}
