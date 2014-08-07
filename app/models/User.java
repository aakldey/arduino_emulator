package models;

import javax.persistence.*;

import controllers.Application;
import controllers.Arduino;
import play.data.Form;
import play.data.validation.Constraints.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

import java.util.*;

@Entity
public class User extends Model {

    public static Finder<Long, User> find = new Finder<>(Long.class, User.class);

    public static class LoginForm {

        @Required
        public String username;

        @Required
        public String password;
    }

    public static Form<LoginForm> FORM = Form.form(LoginForm.class);

    @Id
    public Long id;

    public String username;

    public String password;

    public User(String login, String password) {
        this.username = login;
        this.password = Application.md5(password);
        this.save();
    }

    public User(LoginForm form) {
        this.username = form.username;
        this.password = Application.md5(form.password);
    }

    public static boolean validate(LoginForm form) {
        String login = form.username;
        String password = Application.md5(form.password);

        User user = User.find.where().eq("username", login).eq("password", password).findUnique();
        if (user == null)
            return false;
        else
            return true;
    }

}