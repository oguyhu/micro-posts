package controllers

import java.time.ZonedDateTime
import javax.inject._

import jp.t2v.lab.play2.auth.AuthenticationElement
import models.Favorite
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import services.{FavoritesService, UserService}

@Singleton
class FavoriteController @Inject()(val favoritesService: FavoritesService,
                                   val userService: UserService,
                                   components: ControllerComponents)
  extends AbstractController(components)
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  def addToFavorites(microPostId: Long): Action[AnyContent] = StackAction { implicit request =>
    val currentUser = loggedIn
    val now         = ZonedDateTime.now()
    val favorite  = Favorite(None, currentUser.id.get, microPostId, now, now)
    favoritesService
      .create(favorite)
      .map { _ =>
        request.headers.get("Referer") match {
          case Some(r) => Redirect(r)
          case _ => Redirect(routes.HomeController.index())
        }
      }.recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.HomeController.index())
            .flashing("failure" -> Messages("InternalError"))
      }.getOrElse(InternalServerError(Messages("InternalError")))
  }

  def deleteFromFavorites(microPostId: Long): Action[AnyContent] = StackAction { implicit request =>
    val currentUser = loggedIn
    favoritesService
      .deleteBy(currentUser.id.get, microPostId)
      .map { _ =>
        request.headers.get("Referer") match {
          case Some(r) => Redirect(r)
          case _ => Redirect(routes.HomeController.index())
        }
      }.recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.HomeController.index())
            .flashing("failure" -> Messages("InternalError"))
      }.getOrElse(InternalServerError(Messages("InternalError")))
  }

}