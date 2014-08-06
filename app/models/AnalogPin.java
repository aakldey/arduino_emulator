package models;

import javax.persistence.*;

import controllers.Arduino;
import play.data.validation.Constraints.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

import java.util.*;

@Entity
public class AnalogPin extends Model {

    public static Finder<Long, AnalogPin> find = new Finder<>(Long.class, AnalogPin.class);

    @Id
    public Long id;

    public int pinNumber = 0;

    public int pinValue = 0;

    public boolean generated = false;

}