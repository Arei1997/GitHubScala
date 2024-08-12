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
//  def read(id: String): Future[Either[APIError.BadAPIResponse, User]]
//  def update(id: String, book: User): Future[Either[APIError.BadAPIResponse, Long]]
//  def delete(id: String): Future[Either[APIError.BadAPIResponse, Long]]
//  def deleteAll(): Future[Unit]
//  def findByName(name: String): Future[Either[APIError.BadAPIResponse, Seq[User]]]
//  def updateField(id: String, fieldName: String, newValue: JsValue): Future[Either[APIError.BadAPIResponse, Long]]
//}

@Singleton
class DataRepository @Inject() (
                                 mongoComponent: MongoComponent
                               )(implicit ec: ExecutionContext) extends PlayMongoRepository[User](
  collectionName = "Users",
  mongoComponent = mongoComponent,
  domainFormat = User.format, // Ensure this is correctly referenced
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
)  {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[User]]] =
    collection.find().toFuture().map {
      case books: Seq[User] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: User): Future[Either[APIError.BadAPIResponse, User]] =
    collection
      .insertOne(book)
      .toFuture()
      .map { _ =>Right(book)
      }.map(_ => Right(book))



  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )


  def read(id: String): Future[Either[APIError.BadAPIResponse, User]] = {
    collection.find(equal("_id", id)).headOption().map {
      case Some(user: User) => Right(user)
      case None => Left(APIError.BadAPIResponse(404, "User not found"))
    }.recover {
      case e: Throwable => Left(APIError.BadAPIResponse(500, e.getMessage))
    }
  }







  def update(id: String, book: User): Future[Either[APIError.BadAPIResponse, Long]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true)
    ).toFuture().map { updateResult =>
      if (updateResult.getModifiedCount > 0) Right(updateResult.getModifiedCount)
      else Left(APIError.BadAPIResponse(404, "User not found or not modified"))
    }


  def delete(id: String): Future[Either[APIError.BadAPIResponse, Long]] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture().map { deleteResult =>
      if (deleteResult.getDeletedCount > 0) Right(deleteResult.getDeletedCount)
      else Left(APIError.BadAPIResponse(404, "User not found"))
    }


  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests





}