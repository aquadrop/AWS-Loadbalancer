package ece1779.ec2.models;

import java.util.List;

public class InstanceMonitor {

	private List<String> runningInstanceIds;
	
	public List<String> getRunningInstanceIds(){
		return this.runningInstanceIds;
	}
	
	public void setRunningInstanceIds(List<String> runningInstanceIds){
		 this.runningInstanceIds = runningInstanceIds;
	}
}
