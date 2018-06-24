package lego_behavior;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class SearchLine implements Behavior {

	// Sensorvariablen
	SensorModes colorSensor1;
	SensorModes colorSensor2;
	SampleProvider col1;
	SampleProvider col2;
	float[] sampleLeft;
	float[] sampleRight;
	int left = 0;
	int right = 0;
	
	//Distanz nach der das Verhalten angenommen wird
	int takeControlDistance = 500;

	private boolean suppressed = false;

	int white = 6;
	int black = 7;
	int green = 1;

	public SearchLine(SensorModes colorSensor1, SensorModes colorSensor2) {
		// initialisieren der Sensoren
		this.colorSensor1 = colorSensor1;
		this.colorSensor2 = colorSensor2;

		this.col1 = this.colorSensor1.getMode("ColorID");
		this.col2 = this.colorSensor2.getMode("ColorID");

		this.sampleLeft = new float[this.colorSensor1.sampleSize()];
		this.sampleRight = new float[this.colorSensor2.sampleSize()];
	}

	@Override
	public void suppress() {
		this.suppressed = true;

	}

	@Override
	public boolean takeControl() {
		// Radumdrehungen werden gemessen
		int tacho = Motor.B.getTachoCount();

		// Farbwerte lesen
		col1.fetchSample(sampleLeft, 0);
		col2.fetchSample(sampleRight, 0);
		left = (int) sampleLeft[0];
		right = (int) sampleRight[0];

		if (left != black && right != black) {
//			LCD.drawString("angle", 0, 3);
//			LCD.drawInt(tacho, 0, 4);
		} else {
			Motor.B.resetTachoCount();
			LCD.clearDisplay();
			LCD.drawString("Line found", 0, 3);
		}

		// Verhalten annehen wenn über angegebene Distanz keine Linie gefunden wurde
		if (tacho >= takeControlDistance) {
			return true;
		} else {
			return false;
		}
	}

	//Funktion des Verhaltens SearchLine
	@Override
	public void action() {
		this.suppressed = false;
//		Thread.yield();
		Motor.B.stop();
		Motor.C.stop();
		
		LCD.clearDisplay();
		LCD.drawString("search now", 0, 2);
		
		// Suche mit Distanz
		this.search(300);
	}

	// Suchmethode
	public void search(int distance) {
		int left = 0;
		int right = 0;

		Motor.B.forward();
		Motor.C.forward();
		
		String drift = "";
		
		
		while (!this.suppressed) {
			// Farbwerte lesen
			col1.fetchSample(sampleLeft, 0);
			col2.fetchSample(sampleRight, 0);
			left = (int) sampleLeft[0];
			right = (int) sampleRight[0];
			
			// Suche abbrechen wenn Line gefunden wurde oder Tischrand erreicht wird 
			if ( left == black || right == black || left == -1 || right == -1) {
				this.suppress();
				return;
			}
						
			int driftDuration = 2000; // ms je nach Geschwindigkeit anpassen
			
			// abwechselnd rechts oder links suchen
			if (drift.equals("")) {
				Motor.C.stop();
				Delay.msDelay(driftDuration / 2);
				Motor.C.forward();
			} else if (drift.equals("right")) {
				Motor.C.stop();
				Delay.msDelay(driftDuration);
				Motor.C.forward();
			} else {
				Motor.B.stop();
				Delay.msDelay(driftDuration);
				Motor.B.forward();
			}
			
			Motor.B.forward();
			Motor.C.forward();
			
			drift = (drift.equals("left"))? "right" : "left";
			Motor.B.resetTachoCount();
			
			// suche solange Distanz noch nicht erreicht 
			while (Motor.B.getTachoCount() < distance && !suppressed) {
				col1.fetchSample(sampleLeft, 0);
				col2.fetchSample(sampleRight, 0);
				left = (int) sampleLeft[0];
				right = (int) sampleRight[0];
				
				// Suche abbrechen wenn Line gefunden wurde oder Tischrand erreicht wird 
				if ( left == black || right == black || left == -1 || right == -1) {
					this.suppress();
					return;
				}
					
				
				// manuell Verhalten stoppen
				if (Button.getButtons() == Button.ID_UP) {
					this.suppress();
					LCD.clear();
					Motor.B.stop();
					Motor.C.stop();
				}
			}
			
			
			// manuell Verhalten stoppen
			if (Button.getButtons() == Button.ID_UP) {
				this.suppress();
				LCD.clear();
				Motor.B.stop();
				Motor.C.stop();
			}
		}
	}
	
	public void safeYourself() {
		Motor.B.backward();
		Motor.C.backward();
		
		Delay.msDelay(3000);
		
		Motor.B.stop();
		Motor.C.stop();
		
		Motor.B.backward();
		Motor.C.forward();		
		
		while(!this.suppressed) {
			col1.fetchSample(sampleLeft, 0);
			col2.fetchSample(sampleRight, 0);
			left = (int) sampleLeft[0];
			right = (int) sampleRight[0];
			
//			LCD.drawString("angle", 0, 3);
//			LCD.drawInt(Motor.B.getTachoCount(), 0, 4);
			
			if (left == black || right == black) {
				Motor.C.backward();
				Delay.msDelay(500);
				this.suppress();
			}
			
			// manuell Verhalten stoppen
			if (Button.getButtons() == Button.ID_UP) {
				this.suppress();
				LCD.clear();
				Motor.B.stop();
				Motor.C.stop();
			}
		}
	}

}
