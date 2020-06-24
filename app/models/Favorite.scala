package models

import java.time.ZonedDateTime

import scalikejdbc._, jsr310._
import skinny.orm._
import skinny.orm.feature._

case class Favorite(id: Option[Long],
                    userId: Long,
                    microPostId: Long,
                    createAt: ZonedDateTime = ZonedDateTime.now(),
                    updateAt: ZonedDateTime = ZonedDateTime.now(),
                    user: Option[User] = None,
                    microPost: Option[MicroPost] = None)

object Favorite extends SkinnyCRUDMapper[Favorite] {

  lazy val u = User.createAlias("u")

  lazy val userRef = belongsToWithAliasAndFkAndJoinCondition[User](
    right = User -> u,
    fk = "userId",
    on = sqls.eq(defaultAlias.userId, u.id),
    merge = (f, o) => f.copy(user = o)
  ).byDefault

  lazy val m = MicroPost.createAlias("m")

  lazy val microPostRef = belongsToWithAliasAndFkAndJoinCondition[MicroPost](
    right = MicroPost -> m,
    fk = "microPostId",
    on = sqls.eq(defaultAlias.microPostId, m.id),
    merge = (f, o) => f.copy(microPost = o)
  )

  lazy val microPostUserRef =
    belongsTo[MicroPost](MicroPost, (f, m) => f.copy(microPost = m))
      .includes[MicroPost]((favorites, microPosts) => favorites.map { f =>
        microPosts.find(m => f.microPost.exists(_.id == m.id))
          .map(v => f.copy(microPost = Some(v)))
          .getOrElse(f)
      })

  lazy val allAssociations: CRUDFeatureWithId[Long, Favorite] = joins(userRef, microPostRef)

  lazy val allRefIncludes = includes(microPostUserRef)

  override def tableName = "favorites"

  override def defaultAlias: Alias[Favorite] = createAlias("f")

  override def extract(rs: WrappedResultSet, n: ResultName[Favorite]): Favorite =
    autoConstruct(rs, n, "user", "microPost")

  def create(favorite: Favorite)(implicit session: DBSession): Long =
    createWithAttributes(toNamedValues(favorite): _*)

  private def toNamedValues(record: Favorite): Seq[(Symbol, Any)] = Seq(
    'userId      -> record.userId,
    'microPostId -> record.microPostId,
    'createAt    -> record.createAt,
    'updateAt    -> record.updateAt
  )

  def update(favorite: Favorite)(implicit session: DBSession): Int =
    updateById(favorite.id.get).withAttributes(toNamedValues(favorite): _*)

}