package lego_behavior;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;

public class WorldsEnd implements Behavior{

	// Sensorvariablen
		SensorMode sensor1;
//		SensorModes sensor2;
		SampleProvider col1;
//		SampleProvider col2;
		float[] sampleLeft;
		float[] sampleRight;
		
		int left = 0;

		private boolean suppressed = false;

	
	public WorldsEnd(SensorModes sensor1, SensorModes sensor2) {
		// initialisieren der Sensoren
		
		this.sensor1 = sensor1.getMode("Ambient");
		this.sampleLeft = new float[this.sensor1.sampleSize()];
		
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void suppress() {
		this.suppressed = true;
		
	}

	@Override
	public boolean takeControl() {
		this.sensor1.fetchSample(sampleLeft, 0);
		left = (int)this.sampleLeft[0];
		
		LCD.drawString("Ambient", 0, 3);
		LCD.drawInt(left, 7, 3);
		
		return false;
	}
}
