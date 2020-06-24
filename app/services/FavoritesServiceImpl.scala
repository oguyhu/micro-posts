package services

import javax.inject.Singleton
import models.{Favorite, MicroPost, PagedItems, User}
import scalikejdbc._
import skinny.{Pagination, PaginationPageNoBuilder, ParamType}

import scala.util.Try

@Singleton
class FavoritesServiceImpl extends FavoritesService {

  override def create(favorite: Favorite)(implicit dbSession: DBSession): Try[Long] = Try {
    Favorite.create(favorite)
  }

  override def findById(id: Long)(implicit dbSession: DBSession = AutoSession): Try[List[Favorite]] = Try {
    Favorite.where('id -> id).apply()
  }

  override def findByMicroPostId(microPostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[Favorite]] =
    Try {
      Favorite.where('microPostId -> microPostId).apply().headOption
    }

  /** あるユーザーのお気に入りリストを返す */
  override def findFavoritesByUserId(userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[List[Favorite]] = Try {
    Favorite.allAssociations.findAllBy(sqls.eq(Favorite.defaultAlias.userId, userId))
  }

  /** あるユーザーのお気に入りリストのページのitemsであるSeq[MicroPost]を返す。
   *  リストはMicroPostのidの降順にソートされている */
  override def findFavoritesWithPaginationByUserId(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[Seq[MicroPost]] = Try {
    Favorite.allRefIncludes.findAllByWithPagination(
      sqls.eq(Favorite.defaultAlias.userId, userId),
      pagination,
      Seq(Favorite.m.id.desc)).map(_.microPost.getOrElse(throw new Exception("None.getOrElse")))
  }

  /** あるユーザーのお気に入りの数を返す */
  override def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long] = Try {
    Favorite.allAssociations.countBy(sqls.eq(Favorite.defaultAlias.userId, userId))
  }

  override def deleteBy(userId: Long, microPostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int] = Try {
    val c     = Favorite.column
    val count = Favorite.countBy(sqls.eq(c.userId, userId).and.eq(c.microPostId, microPostId))
    if (count == 1) {
      Favorite.deleteBy(
        sqls
          .eq(Favorite.column.userId, userId)
          .and(sqls.eq(Favorite.column.microPostId, microPostId))
      )
    } else 0
  }

}