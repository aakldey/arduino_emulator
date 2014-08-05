package controllers;

import models.AnalogPin;
import models.DigitalPin;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
    public static String FLASH_ERROR_KEY = "error";
    public static String FLASH_MESSAGE_KEY = "message";

    public static Result index() {
        return redirect(routes.Application.config());
    }

    public static Result config() {
        return ok(config.render(AnalogPin.find.all(), DigitalPin.find.all()));
    }

    public static Result jsRoutes() {
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        routes.javascript.Arduino.writeDigitalPin())
                ).as("text/javascript");
    }

}
