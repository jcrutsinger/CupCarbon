package script;

import device.SensorNode;

public class Command_PICK extends Command {

	protected String arg = ""; 
	
	public Command_PICK(SensorNode sensor, String arg) {
		this.sensor = sensor ;
		this.arg = arg ;
	}

	@Override
	public double execute() {
//		int lEvent = sensor.pickMessage(arg);
//		double ratio = (DataInfo.ChDataRate*1.0)/(DataInfo.UartDataRate);
//		return (int)(Math.round(lEvent*8.*ratio));
		sensor.pickMessage(arg);
		return 0 ;
	}

	@Override
	public String toString() {
		return "PICK";
	}
	
}
