package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject

//@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def index(): Action[AnyContent] = TODO
  def create(): Action[AnyContent] = TODO
  def read(): Action[AnyContent] = TODO
  def update(): Action[AnyContent] = TODO
  def delete(): Action[AnyContent] = TODO


}
