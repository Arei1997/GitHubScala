package model

import org.mongodb.scala.bson.ObjectId

case class User(
                 _id: ObjectId = new ObjectId(),
                 username: String,
                 createdAt: String,
                 location: Option[String],
                 followers: Int,
                 following: Int
               )
