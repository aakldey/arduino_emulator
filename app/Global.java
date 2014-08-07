import com.avaje.ebean.Ebean;
import models.User;
import play.*;
import play.libs.Yaml;

import java.util.List;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (User.find.findRowCount() == 0) {
            Ebean.save((List) Yaml.load("initial-data.yml"));
        }
    }

    @Override
    public void onStop(Application app) {

    }
}
