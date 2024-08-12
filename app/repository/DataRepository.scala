package repository


import model.{APIError, User}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{empty, equal}
import org.mongodb.scala.model._
import play.api.libs.json.JsValue
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


//trait MockRepositoryTrait {
//  def index(): Future[Either[APIError.BadAPIResponse, Seq[User]]]
//  def create(user: User: Future[Either[APIError.BadAPIResponse, User]]
//  def read(login: String): Future[Either[APIError.BadAPIResponse, User]]
//  def update(login: String, book: User): Future[Either[APIError.BadAPIResponse, Long]]
//  def delete(login: String): Future[Either[APIError.BadAPIResponse, Long]]
//  def deleteAll(): Future[Unit]
//  def findByName(name: String): Future[Either[APIError.BadAPIResponse, Seq[User]]]
//  def updateField(login: String, fieldName: String, newValue: JsValue): Future[Either[APIError.BadAPIResponse, Long]]
//}

@Singleton
class DataRepository @Inject() (
                                 mongoComponent: MongoComponent
                               )(implicit ec: ExecutionContext) extends PlayMongoRepository[User](
  collectionName = "Users",
  mongoComponent = mongoComponent,
  domainFormat = User.format, // Ensure this is correctly referenced
  indexes = Seq(IndexModel(
    Indexes.ascending("login")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[User]]] =
    collection.find().toFuture().map {
      case users: Seq[User] => Right(users)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(user: User): Future[Either[APIError.BadAPIResponse, User]] =
    collection
      .insertOne(user)
      .toFuture()
      .map { _ => Right(user)
      }.map(_ => Right(user))


  private def byID(login: String): Bson =
    Filters.and(
      Filters.equal("login", login)
    )


  def read(login: String): Future[Either[APIError.BadAPIResponse, User]] = {
    collection.find(equal("login", login)).headOption().map {
      case Some(user: User) => Right(user)
      case None => Left(APIError.BadAPIResponse(404, "User not found"))
    }.recover {
      case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
    }
  }


  def update(login: String, user: User): Future[Either[APIError.BadAPIResponse, Long]] =
    collection.replaceOne(
      filter = byID(login),
      replacement = user,
      options = new ReplaceOptions().upsert(true)
    ).toFuture().map { updateResult =>
      if (updateResult.getModifiedCount > 0) Right(updateResult.getModifiedCount)
      else Left(APIError.BadAPIResponse(404, "User not found or not modified"))
    }


  def delete(login: String): Future[Either[APIError.BadAPIResponse, Long]] =
    collection.deleteOne(
      filter = byID(login)
    ).toFuture().map { deleteResult =>
      if (deleteResult.getDeletedCount > 0) Right(deleteResult.getDeletedCount)
      else Left(APIError.BadAPIResponse(404, "User not found"))
    }


  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests


}