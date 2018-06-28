package lego_behavior;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Bumper implements Behavior {
	private boolean suppressed = false;

	EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
	SensorMode touch = touchSensor.getTouchMode();

	float pressed = 0;
	float sample[] = new float[touch.sampleSize()];

	// Farbsensorvariablen
	SensorModes colorSensor1;
	SensorModes colorSensor2;
	SampleProvider col1;
	SampleProvider col2;
	float[] sampleLeft;
	float[] sampleRight;
	int left = 0;
	int right = 0;

	int white = 6;
	int black = 7;
	int green = 1;

	public Bumper(SensorModes colorSensor1, SensorModes colorSensor2) {
		// initialisieren der Sensoren
		this.colorSensor1 = colorSensor1;
		this.colorSensor2 = colorSensor2;

		this.col1 = this.colorSensor1.getMode("ColorID");
		this.col2 = this.colorSensor2.getMode("ColorID");

		this.sampleLeft = new float[this.colorSensor1.sampleSize()];
		this.sampleRight = new float[this.colorSensor2.sampleSize()];
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
		turnRight();
		bothMotorsForeward(3300);
		turnLeft();
		bothMotorsForeward(5000);
		turnLeft();
		bothMotorsForeward(3000);
		turnRight();

		while (!suppressed) {
			col1.fetchSample(sampleLeft, 0);
			col2.fetchSample(sampleRight, 0);
			left = (int) sampleLeft[0];
			right = (int) sampleRight[0];
			
			if (left == -1 || right == -1) {
				bothMotorsStop();
				LCD.drawString("stop", 0, 3);
				this.suppress();
			} else if(left == black || right == black ) {
				LCD.drawString("turn right", 0, 3);
//				turnRight();
				this.suppress();
			} else {
				LCD.drawString("white found", 0, 3);
			}
			LCD.clear();
		}

		touch.fetchSample(sample, 0);

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
		Motor.B.forward();
		Motor.C.backward();
		Delay.msDelay(1900); // ms je nach Geschwindigkeit anpassen
	}

	public void turnRight() {
		Motor.C.forward();
		Motor.B.backward();
		Delay.msDelay(1900); // ms je nach Geschwindigkeit anpassen
	}

}