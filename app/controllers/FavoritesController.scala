package controllers

import javax.inject._
import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.{Favorite, MicroPost, PagedItems}
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import services.{FavoritesService, MicroPostService, UserService}
import skinny.Pagination

import scala.util.Success

@Singleton
class FavoritesController @Inject()(val userService: UserService,
                                    val microPostService: MicroPostService,
                                    val favoritesService: FavoritesService,
                                    components: ControllerComponents)
  extends AbstractController(components)
    with I18nSupport
    with AuthConfigSupport
    with OptionalAuthElement {

  private val postForm = Form {
    "content" -> nonEmptyText
  }

  // userIdのユーザーのお気に入りリストを表示する
  // ログインユーザーを取得できなかった場合、最初のページを表示する
  // ページのitemsが0だった場合、前のページがあればそのページを表示する
  def index(userId: Long, page: Int): Action[AnyContent] = StackAction { implicit request =>
    val userOpt = loggedIn
    userOpt.map { currentUser =>
      val pageSize = 10
      val pagination = Pagination(pageSize, page)
      (for {
        size <- favoritesService.countByUserId(userId)
        favorites <- favoritesService.findFavoritesByUserId(userId)
        items <- favoritesService.findFavoritesWithPaginationByUserId(pagination, userId)
      } yield {
        val microPosts = PagedItems(pagination, size, items)
        if (size != 0 && items.isEmpty && microPosts.hasPrevious) {
          Redirect(routes.FavoritesController.index(userId, page - 1))
        } else {
          Ok(views.html.favorite.show_favorites(currentUser, favorites, microPosts))
        }        
      }).recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.HomeController.index(page))
            .flashing("failure" -> Messages("InternalError"))
      }.getOrElse(InternalServerError(Messages("InternalError")))
    }.getOrElse(
      Ok(views.html.index(userOpt, postForm, List.empty[Favorite], PagedItems(Pagination(10, page), 0, Seq.empty[MicroPost])))
    )
  }

}