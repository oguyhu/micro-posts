package services

import models.{Favorite, MicroPost, PagedItems, User}
import scalikejdbc.{AutoSession, DBSession}
import skinny.Pagination

import scala.util.Try

trait FavoritesService {

  def create(favorite: Favorite)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def findById(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[List[Favorite]]

  def findByMicroPostId(microPostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[Favorite]]

  def findFavoritesByUserId(userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[List[Favorite]]

  def findFavoritesWithPaginationByUserId(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[Seq[MicroPost]]

  def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def deleteBy(userId: Long, microPostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int]

}