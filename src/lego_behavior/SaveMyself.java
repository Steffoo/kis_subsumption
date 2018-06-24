package lego_behavior;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class SaveMyself implements Behavior {
	
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

	public SaveMyself(SensorModes colorSensor1, SensorModes colorSensor2) {
		// initialisieren der Sensoren
		this.colorSensor1 = colorSensor1;
		this.colorSensor2 = colorSensor2;

		this.col1 = this.colorSensor1.getMode("ColorID");
		this.col2 = this.colorSensor2.getMode("ColorID");

		this.sampleLeft = new float[this.colorSensor1.sampleSize()];
		this.sampleRight = new float[this.colorSensor2.sampleSize()];
	}

	@Override
	public void action() {
		Motor.B.backward();
		Motor.C.backward();
		
		Delay.msDelay(3000);
		
		Motor.B.stop();
		Motor.C.stop();
		
		Motor.B.backward();
		Motor.C.forward();		
		
		LCD.clearDisplay();
		
		while(!this.suppressed) {
			col1.fetchSample(sampleLeft, 0);
			col2.fetchSample(sampleRight, 0);
			left = (int) sampleLeft[0];
			right = (int) sampleRight[0];
			
			LCD.drawString("angle", 0, 4);
			LCD.drawInt(Motor.B.getTachoCount(), 8, 4);
			
			if (left == black || right == black) {
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
		

	@Override
	public void suppress() {
		this.suppressed = true;
		
	}

	@Override
	public boolean takeControl() {

		// Farbwerte lesen
		col1.fetchSample(sampleLeft, 0);
		col2.fetchSample(sampleRight, 0);
		left = (int) sampleLeft[0];
		right = (int) sampleRight[0];

		// Verhalten annehen wenn Tischrand erreicht wird
		if ( left == -1 || right ==-1) {
			return true;
		} else {
			return false;
		}
	}

}
