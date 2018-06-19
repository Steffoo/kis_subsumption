package lego_behavior;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Bumper implements Behavior {
	private boolean suppressed = false;
	
	EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
	SensorMode touch = touchSensor.getTouchMode();
	
	float pressed = 0;
	float sample[] = new float[touch.sampleSize()];
	
	public Bumper() {
	}

    public boolean takeControl() {
    		touch.fetchSample(sample, 0);
    		this.pressed = sample[0];
    		return this.pressed == 1.0;
    }

    public void suppress() {
       this.suppressed = true;
    }

    // Funktion des Verhaltens Bumper
    public void action() {
        bothMotorsBackward(700);
        turnLeft();
        bothMotorsForeward(2500);
        turnRight();
        bothMotorsForeward(5500);
        turnRight();
        bothMotorsForeward(2500);
        turnLeft();
        bothMotorsStop();
	    touch.fetchSample(sample, 0);
        this.suppress();
        
    }
    
    
	public void bothMotorsStop() {
		Motor.B.stop();
		Motor.C.stop();
	}

	public void bothMotorsForeward(int duration) {
		Motor.B.forward();
		Motor.C.forward();
		Delay.msDelay(duration);
	}
	
	public void bothMotorsBackward(int duration) {
		Motor.B.backward();
		Motor.C.backward();
		Delay.msDelay(duration);
	}
	
	public void turnLeft() {
		Motor.C.forward();	
	    Motor.B.backward();
	    Delay.msDelay(1900);   	// ms je nach Geschwindigkeit anpassen
	}
	
	public void turnRight() {
		Motor.B.forward();		
	    Motor.C.backward();
	    Delay.msDelay(1900);		// ms je nach Geschwindigkeit anpassen
	}
	
}