package lego_behavior;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.HiTechnicAccelerometer;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Accel implements Behavior {

	SensorModes accelSensor = new HiTechnicAccelerometer(SensorPort.S4);
	float[] accelSample = new float[3];
	int accel = 0;
	boolean suppressed = false;
	int slow;
	int fast;
	
	public Accel(int slow, int fast) {
		this.slow = slow;
		this.fast = fast;
	}
	
	@Override
	public void action() {
		this.suppressed = false;
		// beschleunigen für Rampe
		Motor.B.setSpeed(fast);
		Motor.C.setSpeed(fast + 80);
		Delay.msDelay(1000);
		
		while(!suppressed) {
			
			accelSensor.fetchSample(accelSample, 0);
			accel = (int) accelSample[0];

			LCD.drawString("accel", 0, 2);
			LCD.drawInt(accel, 0, 3);

			// wenn auf gerader Ebene wieder verlangsamen
			if (accel >= 0) {
				this.suppress();
			}
		}
	}

	@Override
	public void suppress() {
		Motor.B.setSpeed(slow);
		Motor.C.setSpeed(slow);
		this.suppressed = true;
	}

	@Override
	public boolean takeControl() {
		accelSensor.fetchSample(accelSample, 0);
		accel = (int) accelSample[0];

		LCD.drawString("accel", 0, 2);
		LCD.drawInt(accel, 0, 3);

		if (accel <= -4) {
			return true;
		} else { 
			return false;
		}
	}

}
