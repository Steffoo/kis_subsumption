package lego_behavior;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class LineFollower implements Behavior{

	// Einstellungen für das primitive Verhalten
	private int TURN_DELAY = 1500;
	private boolean suppressed = false;
	boolean stopArby = false;
	
	// Sensorvariablen
	SensorModes colorSensor1;
	SensorModes colorSensor2;
	SampleProvider col1;
	SampleProvider col2;
	float[] sampleLeft;
	float[] sampleRight;
	
	public LineFollower(SensorModes colorSensor1, SensorModes colorSensor2) {
		// initialisieren der Sensoren
		this.colorSensor1 = colorSensor1;
		this.colorSensor2 = colorSensor2;
		
		this.col1 = this.colorSensor1.getMode("ColorID");
		this.col2 = this.colorSensor2.getMode("ColorID");
		
		int size1 = this.colorSensor1.sampleSize();
		int size2 = this.colorSensor2.sampleSize();

		this.sampleLeft = new float[size1];
		this.sampleRight = new float[size2];
	}
	

	public boolean takeControl() {
		/*
		 *  Falls nicht manuell beendet wird, wird der Arbitrator 
		 *  immer wieder dieses Verhalten annehmen
		 */
		if(this.stopArby) {
			return false;
		} else {
			return true;	
		}
	}
	
	public void suppress() {
		this.suppressed = true;
	}

	
	// Funktion des Verhaltens LineFollower
	public void action() {
		LCD.clear();
		this.suppressed = false;

		int size1 = colorSensor1.sampleSize();
		float[] sampleLeft = new float[size1];

		int size2 = colorSensor2.sampleSize();
		float[] sampleRight = new float[size2];
		
		int white = 6;
		int black = 7;
		int green = 1;

		bothMotorsForward();

		// solange nicht manuell abgebrochen wird, wird der Linie gefolgt
		while (!this.suppressed) {
			Thread.yield();
			LCD.clear();
			
			
			// Farbwerte lesen
			col1.fetchSample(sampleLeft, 0);
			col2.fetchSample(sampleRight, 0);
			int left = (int) sampleLeft[0];
			int right = (int) sampleRight[0];

			
			// grün = Abbiegen auf die Seite des grünen Symbols
			if (left == green || right == green) {
				if (left == green) {
					Motor.C.stop();
					Motor.B.forward();
					Delay.msDelay(TURN_DELAY);
				} else {
					Motor.B.stop();
					Motor.C.forward();
					Delay.msDelay(TURN_DELAY);
				}
			// beide Seiten schwarz = weiter geradeaus	
			} else if (right == black && left == black) {
				Delay.msDelay(800);
			// beide Seiten weiss = beide Motoren geradeaus 	
			} else if (left == white && right == white) {
				bothMotorsForward();
			// rechts = schwarz
			} else if (left == white && right == black) {
				Motor.B.stop();
				Motor.C.forward();
				// drehen bis rechts wieder weiss
				while (right == black) {
					col1.fetchSample(sampleLeft, 0);
					col2.fetchSample(sampleRight, 0);
					left = (int) sampleLeft[0];
					right = (int) sampleRight[0];
				}

				Motor.B.forward();
			// links = schwarz
			} else if (right == white && left == black) {
				Motor.C.stop();
				Motor.B.forward();
				// drehen bis links wieder weiss
				while (left == black) {
					col1.fetchSample(sampleLeft, 0);
					col2.fetchSample(sampleRight, 0);
					left = (int) sampleLeft[0];
					right = (int) sampleRight[0];
				}

				Motor.C.forward();

			}			
	
			// manuell Verhalten beenden
			if (Button.getButtons() == Button.ID_UP) {
				this.stopArby = true;
				this.suppress();
				LCD.clear();
				bothMotorsStop();
			}

		}

	}

	
	
	public void bothMotorsStop() {
		Motor.B.stop();
		Motor.C.stop();
	}

	public void bothMotorsForward() {
		Motor.B.forward();
		Motor.C.forward();
	}
	
}
