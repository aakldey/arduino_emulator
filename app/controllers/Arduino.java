package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.AnalogPin;
import models.DigitalPin;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
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

    public static Result readAnalogPin(int pinNumber) {
        ObjectNode result = Json.newObject();
        AnalogPin pin = AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique();
        if(pin != null) {
            result.put("a"+pinNumber, pin.pinValue);
            return ok(result);
        } else {
            return badRequest();
        }
    }

    public static Result writeAnalogPin(int pinNumber, int value) {
        if (value < 0 || value > 1023)
            return badRequest();
        AnalogPin pin = AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique();

        if(pin != null) {
            pin.pinValue = value;
            pin.save();
            return ok(Integer.toString(value));
        } else {
            return badRequest();
        }
    }

    public static Result addDigitalPin() {
        DynamicForm form = Form.form().bindFromRequest();
        try {
            int pinNumber = Integer.parseInt(form.get("pinNumber"));
            String value = form.get("value");
            if(!value.equals(DIGITAL_HIGH) && !value.equals(DIGITAL_LOW)) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
                return redirect(routes.Application.config());
            }

            if (DigitalPin.find.where().eq("pinNumber", pinNumber).findUnique() != null) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.error.connected"));
                return redirect(routes.Application.config());
            }

            if (pinNumber < 1 || pinNumber > 15) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
                return redirect(routes.Application.config());
            }

            DigitalPin pin = new DigitalPin();
            pin.pinNumber = pinNumber;
            pin.pinState = value;
            pin.save();

            flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.added"));
            return redirect(routes.Application.config());


        } catch (Exception e) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
            return redirect(routes.Application.config());
        }

    }

    public static Result addAnalogPin() {
        DynamicForm form = Form.form().bindFromRequest();
        try{
            int number = Integer.parseInt(form.get("pinNumber"));
            int value = Integer.parseInt(form.get("value"));

            if (AnalogPin.find.where().eq("pinNumber", number).findUnique() != null) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.error.connected"));
                return redirect(routes.Application.config());
            }

            if (value < 1 || value > 1023) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
                return redirect(routes.Application.config());
            }

            AnalogPin pin = new AnalogPin();

            pin.pinNumber = number;
            pin.pinValue = value;
            pin.save();

            flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.added"));
            return redirect(routes.Application.config());

        } catch (Exception e) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
            return redirect(routes.Application.config());
        }
    }

    public static Result removeDigitalPin(int pinNumber) {
        if (DigitalPin.find.where().eq("pinNumber", pinNumber).findUnique() == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
            return redirect(routes.Application.config());
        }

        DigitalPin.find.where().eq("pinNumber", pinNumber).findUnique().delete();

        flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.deleted"));
        return redirect(routes.Application.config());
    }

    public static Result removeAnalogPin(int pinNumber) {
        if (AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique() == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
            return redirect(routes.Application.config());
        }

        AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique().delete();

        flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.deleted"));
        return redirect(routes.Application.config());
    }

}
