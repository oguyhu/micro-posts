package controllers

import javax.inject._
import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.{Favorite, MicroPost, PagedItems}
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import play.api.mvc.Call
import services.{FavoritesService, MicroPostService, UserService}
import skinny.Pagination

@Singleton
class HomeController @Inject()(val userService: UserService,
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

  def index(page: Int): Action[AnyContent] = StackAction { implicit request =>
    val userOpt = loggedIn
    userOpt.fold {
      Ok(views.html.index(userOpt, postForm, List.empty[Favorite], PagedItems(Pagination(10, page), 0, Seq.empty[MicroPost])))
    } { user =>
      val triedFavorites = favoritesService.findFavoritesByUserId(user.id.get)
      val triedPagedItems = microPostService.findAllByWithLimitOffset(Pagination(10, page), user.id.get)
      (for {
        favorites <- triedFavorites
        pagedItems <- triedPagedItems
      } yield {
        Ok(views.html.index(userOpt, postForm, favorites, pagedItems))
      }).recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.HomeController.index(page))
            .flashing("failure" -> Messages("InternalError"))
      }.getOrElse(InternalServerError(Messages("InternalError")))
    }
  }

}