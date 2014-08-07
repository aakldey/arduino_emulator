package controllers;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jobs.RandomAnalogActor;
import models.AnalogPin;
import models.DigitalPin;
import models.RandomAnalog;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Akka;
import play.mvc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.*;
import views.html.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.concurrent.TimeUnit;

public class Arduino extends Controller {

    public static String DIGITAL_HIGH = "HIGH";
    public static String DIGITAL_LOW = "LOW";

    private static Map<AnalogPin, Cancellable> jobs = new HashMap<>();

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

    public static Result writeAnalogPin(int pinNumber, String value) {
        try {
            int val = Integer.parseInt(value);

            if (val <= 0 || val > 1023)
                return badRequest((Messages.get("config.error.outofrange")));

            AnalogPin pin = AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique();

            if (pin != null) {
                pin.pinValue = val;
                pin.save();
                return ok(Integer.toString(val));
            } else {
                return badRequest(Messages.get("config.error.connected "));
            }
        } catch (Exception e) {
            return badRequest(Messages.get("config.error.notanumber"));
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
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.error.outofrange"));
                return redirect(routes.Application.config());
            }

            AnalogPin pin = new AnalogPin();

            pin.pinNumber = number;
            pin.pinValue = value;
            pin.save();

            flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.added"));
            return redirect(routes.Application.config());

        } catch (Exception e) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("config.error.notanumber"));
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

    public static Result addRandom(int pinNumber) {
        try {
            DynamicForm form = Form.form().bindFromRequest();
            int timeInterval = Integer.parseInt(form.get("time"));
            int randStart = Integer.parseInt(form.get("randomStart"));
            int randEnd = Integer.parseInt(form.get("randomEnd"));

            AnalogPin pin = AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique();

            if (pin != null) {

                pin.generated = true;
                pin.save();

                RandomAnalog rand = new RandomAnalog(pin.pinNumber, randStart, randEnd);

                ActorRef jobactor = Akka.system().actorOf(Props.create(RandomAnalogActor.class), "pin"+pin.pinNumber);
                Cancellable tick = Akka.system().scheduler().schedule(
                        Duration.create(0, TimeUnit.SECONDS),
                        Duration.create(timeInterval, TimeUnit.SECONDS),
                        jobactor,
                        rand,
                        Akka.system().dispatcher(),
                        null
                );

                jobs.put(pin, tick);

                flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.success"));
                return redirect(routes.Application.config());
            } else {
                flash(Application.FLASH_ERROR_KEY, Messages.get("config.error.nopin"));
                return redirect(routes.Application.config());
            }

        } catch (Exception e) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("config.fail"));
            return redirect(routes.Application.config());
        }
    }

    public static Result removeRandom(int pinNumber) {
        AnalogPin pin = AnalogPin.find.where().eq("pinNumber", pinNumber).findUnique();

        if (jobs.containsKey(pin)) {
            jobs.get(pin).cancel();
            jobs.remove(pin);
            pin.generated = false;
            pin.save();
        }

        flash(Application.FLASH_MESSAGE_KEY, Messages.get("config.success"));
        return redirect(routes.Application.config());
    }

    public static Result getList() {
        ObjectNode result = Json.newObject();

        JsonNode digital = Json.toJson(DigitalPin.find.all());
        JsonNode analog = Json.toJson(AnalogPin.find.all());


        result.put("digital", digital);
        result.put("analog", analog);

        return ok(result);
    }

}
