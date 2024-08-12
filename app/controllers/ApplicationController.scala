package controllers

import model.APIError
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import service.LibraryService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

//@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, libraryService: LibraryService)(implicit ec: ExecutionContext) extends BaseController {

  def getGitHubUser(username: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    libraryService.getGithubUser(username).value.map {
      case Right(user) => Ok(views.html.gitHubUser(user))
      case Left(APIError.BadAPIResponse(status, message)) => Status(status)(Json.obj("error" -> message))
    }
  }

  def index(): Action[AnyContent] = TODO
  def create(): Action[AnyContent] = TODO
  def read(): Action[AnyContent] = TODO
  def update(): Action[AnyContent] = TODO
  def delete(): Action[AnyContent] = TODO


}
