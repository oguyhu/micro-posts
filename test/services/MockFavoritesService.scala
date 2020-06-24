package services

import java.time.ZonedDateTime

import models.{Favorite, MicroPost}
import scalikejdbc.{AutoSession, DBSession}
import skinny.Pagination

import scala.util.Try

class MockFavoritesService extends FavoritesService {

  def create(favorite: Favorite)(implicit dbSession: DBSession = AutoSession): Try[Long] = Try(1L)

  def findById(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[List[Favorite]] =
    Try(List(Favorite(Some(1L), 1L, 1L)))

  def findByMicroPostId(microPostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[Favorite]] =
    Try(Some(Favorite(Some(1L), 1L, 1L)))

  def findFavoritesByUserId(userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[List[Favorite]] = Try(List(Favorite(Some(1L), 1L, 1L)))

  def findFavoritesWithPaginationByUserId(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[Seq[MicroPost]] = Try(Seq(MicroPost(Some(1L), 1L, "a", ZonedDateTime.now, ZonedDateTime.now)))

  def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long] =
    Try(1L)

  def deleteBy(userId: Long, microPostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int] =
    Try(1)

}