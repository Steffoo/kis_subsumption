package lego_behavior;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.AccelerometerAdapter;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class MainBehavior {
	final static int SLOW = 150;
	final static int FAST = 400;
	
	public static void main(String[] args) {

		/*
		 * Da die Farbsensoren in verschiedenen Verhalten verwendet werden,
		 * müssen die Ports hier zugewiesen werden und über den Konstruktor, den 
		 * Verhalten übergeben werden
		 */
		SensorModes colorSensor1 = new EV3ColorSensor(SensorPort.S1);
		SensorModes colorSensor2 = new EV3ColorSensor(SensorPort.S2);
		
		// Programm erst nach Knopfdruck starten
		LCD.drawString("Hallo Mensch", 0, 1);
		LCD.drawString("Knopf druecken", 0, 2);
		Button.waitForAnyPress();
		Button.discardEvents();
		LCD.clearDisplay();
		
		// erstellen der Verhalten-Objekte
		LineFollower follower = new LineFollower(colorSensor1, colorSensor2);
		SearchLine searchLine = new SearchLine(colorSensor1, colorSensor2);
		Bumper bumper = new Bumper();
		Accel accel = new Accel(SLOW, FAST);
//		WorldsEnd end = new WorldsEnd(colorSensor1, colorSensor2);
		
		Motor.C.setSpeed(SLOW);
		Motor.B.setSpeed(SLOW);

		Behavior[] bArray = { follower, searchLine, bumper, accel };
//		Behavior[] bArray = { end };

		// Arbitratior steuert die Threads - true dass er nicht in eine Endlosschleife läuft
		Arbitrator arby = new Arbitrator(bArray, true);
//		Arbitrator arby = new Arbitrator(bArray);
		arby.go();
		LCD.clear();
		LCD.drawString("Arbitrator exit!", 0, 2);
		Delay.msDelay(1500);
		LCD.clear();		

	}
}
