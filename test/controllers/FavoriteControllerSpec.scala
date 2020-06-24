package controllers

import akka.stream.Materializer
import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import scalikejdbc.PlayModule
import services._



class FavoriteControllerSpec extends FunSpec
  with MustMatchers
  with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .disable[PlayModule]
      .overrides(bind[FavoritesService].to[MockFavoritesService])
      .build()

  describe("FavoriteController") {
    describe("FavoriteController#addToFavorites") {
      it("should be valid") {
        val updateStatusController = app.injector.instanceOf[FavoriteController]
        val result = updateStatusController.addToFavorites(1L).apply(FakeRequest().withCSRFToken)
        status(result) mustBe SEE_OTHER
      }
    }
    describe("route of FavoriteController#addToFavorites") {
      it("should be valid") {
        val result = route(app,
          FakeRequest(POST, routes.FavoriteController.addToFavorites(1L).toString)).get
        status(result) mustBe SEE_OTHER
      }
    }
    describe("FavoriteController#deleteFromFavorites") {
      it("should be valid") {
        val updateStatusController = app.injector.instanceOf[FavoriteController]
        val result = updateStatusController.deleteFromFavorites(1L).apply(FakeRequest().withCSRFToken)
        status(result) mustBe SEE_OTHER
      }
    }
    describe("route of FavoriteController#deleteFromFavorites") {
      it("should be valid") {
        val result = route(app,
          FakeRequest(POST, routes.FavoriteController.deleteFromFavorites(1L).toString)).get
        status(result) mustBe SEE_OTHER
      }
    }
  }

}