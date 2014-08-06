package jobs;

import akka.actor.UntypedActor;
import models.AnalogPin;
import models.RandomAnalog;

public class RandomAnalogActor extends UntypedActor {

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof RandomAnalog) {
            RandomAnalog rnd = (RandomAnalog)msg;
            AnalogPin pin = AnalogPin.find.where().eq("pinNumber", rnd.getPinNumber()).findUnique();
            int value = rnd.getRangeStart() + (int)(Math.random()*((rnd.getRangeEnd() - rnd.getRangeStart()) + 1));
            pin.pinValue = value;
            pin.save();
        }
    }
}
