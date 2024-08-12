package service

import connector.LibraryConnector
import model.User

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.JsValue

class LibraryService @Inject()(libraryConnector: LibraryConnector)(implicit ec: ExecutionContext) {

  def getGitHubUser(username: String): Future[Option[User]] = {
    libraryConnector.fetchGitHubUser(username).map {
      case Some(userJson) => Some(userJson.as[User])
      case None => None
    }
  }
}
