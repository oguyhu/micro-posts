package controllers

import akka.stream.Materializer
import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import play.api.test._
import scalikejdbc.PlayModule
import services._



class FavoritesControllerSpec extends FunSpec
  with MustMatchers
  with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .disable[PlayModule]
      .overrides(bind[FavoritesService].to[MockFavoritesService])
      .build()

  describe("FavoritesController") {
    describe("route of FavoritesController#index") {
      it("should be valid") {
        val result = route(app,
          FakeRequest(GET, routes.FavoritesController.index(1L).toString)).get
        status(result) mustBe OK
      }
    }
  }

}