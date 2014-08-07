package controllers;

import play.*;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        ctx.flash().put(Application.FLASH_ERROR_KEY, Messages.get("error.unauthorized"));
        return redirect(routes.Application.login());
    }

}
