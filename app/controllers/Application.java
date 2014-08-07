package controllers;

import models.AnalogPin;
import models.DigitalPin;
import models.User;
import org.apache.commons.lang3.text.WordUtils;
import play.*;
import play.api.mvc.Session;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.*;

import views.html.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class Application extends Controller {
    public static String FLASH_ERROR_KEY = "error";
    public static String FLASH_MESSAGE_KEY = "message";

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return redirect(routes.Application.config());
    }

    @Security.Authenticated(Secured.class)
    public static Result config() {
        return ok(config.render(AnalogPin.find.all(), DigitalPin.find.all()));
    }

    public static Result login() {
        return ok(login.render(User.FORM));
    }

    public static Result doLogin() {
        Form<User.LoginForm> loginForm = User.FORM.bindFromRequest();
        if (loginForm.hasErrors()) {
            String errors = "";
            for(Map.Entry<String, List<ValidationError>> entry : loginForm.errors().entrySet()) {
                Logger.info(entry.getKey()+":\n");
                for(ValidationError er : entry.getValue()) {
                    errors += Messages.get(er.message(),WordUtils.capitalize(entry.getKey()));

                }
                errors += " <br>";
            }
            flash(Application.FLASH_ERROR_KEY, errors);
            return badRequest(login.render(loginForm));
        } else {
            if (User.validate(loginForm.get())) {
                session().clear();
                session("username", loginForm.get().username);
                flash(FLASH_MESSAGE_KEY, Messages.get("login.success", loginForm.get().username));
                return redirect(routes.Application.config());
            } else {

            }
        }
        return TODO;
    }

    @Security.Authenticated(Secured.class)
    public static Result logout() {
        session().remove("username");
        return redirect(routes.Application.login());
    }

    public static User getLocalUser(play.mvc.Http.Session session) {
        if (session.get("username") != null) {
            return User.find.where().eq("username", session.get("username")).findUnique();
        } else
            return null;
    }

    public static String md5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch(NoSuchAlgorithmException e) {
            return "";
        }
    }

}
