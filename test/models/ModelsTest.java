package models;

import controllers.Application;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

 public class ModelsTest extends WithApplication {

     @Before
     public void setUp() {
         start(fakeApplication(inMemoryDatabase()));
     }

     @Test
     public void createUser() {
         User user = new User("admin","123");
         User admin = User.find.where().eq("login", "admin").findUnique();
         assertNotNull(admin);
         assertEquals(Application.md5("123"), admin.password);
     }
}
