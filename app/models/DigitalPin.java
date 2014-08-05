package models;

import javax.persistence.*;

import controllers.Arduino;
import play.data.validation.Constraints.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

import java.util.*;

@Entity
public class DigitalPin extends Model {

    public static Finder<Long, DigitalPin> find = new Finder<>(Long.class, DigitalPin.class);

    @Id
    public Long id;

    public int pinNumber = 0;

    public String pinState = Arduino.DIGITAL_LOW;

}