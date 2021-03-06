package script;

import wisen_simulation.SimLog;
import device.SensorNode;

public class Command_DATA extends Command {

	protected String var = "";
	protected String [] args = null;
	
	public Command_DATA(SensorNode sensor, String [] args) {
		this.sensor = sensor ;
		this.var = args[1] ;
		this.args = args ;
	}

	@Override
	public double execute() {		
		String packet= "";
		for(int i=2; i<args.length-1; i++) {
			packet += sensor.getScript().getVariableValue(args[i])+"#";
		}		
		packet += sensor.getScript().getVariableValue(args[args.length-1]);
		sensor.getScript().addVariable(var, packet);
		SimLog.add("S" + sensor.getId() + " DATA Creation:"+packet);
		return 0 ;
	}

	@Override
	public String toString() {
		return "DATA";
	}
	
}
