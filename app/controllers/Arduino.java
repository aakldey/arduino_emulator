package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.DigitalPin;
import play.*;
import play.mvc.*;

import views.html.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class Arduino extends Controller {

    public static String DIGITAL_HIGH = "HIGH";
    public static String DIGITAL_LOW = "LOW";

    public static Result index() {
        return redirect(routes.Application.config());
    }

    public static Result readDigitalPin(int pinNumber) {

        ObjectNode result = Json.newObject();
        DigitalPin pin = DigitalPin.find.where().eq("pinNumber", pinNumber).findUnique();
        if(pin != null) {
            result.put(Integer.toString(pinNumber), pin.pinState );
            return ok(result);
        } else {
            return badRequest();
        }
    }

    public static Result writeDigitalPin(int pinNumber, String value) {
        if(!value.equals(DIGITAL_HIGH) && !value.equals(DIGITAL_LOW))
            return badRequest();

            DigitalPin pin = DigitalPin.find.where().eq("pinNumber", pinNumber).findUnique();
            if (pin != null) {
                pin.pinState = value;
                pin.save();
                return ok(value);
            } else
                return badRequest();
    }

    public static Result readAnalogPin(String pinNumber) {
        return TODO;
    }

    public static Result writeAnalogPin(int pinNumber, int value) {
        if (value < 0 || value > 1023)
            return badRequest();

        return TODO;
    }

}
