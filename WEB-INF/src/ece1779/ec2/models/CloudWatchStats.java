package ece1779.ec2.models;

import java.util.List;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

public class CloudWatchStats {
	
	private String namespace;
	
	private String metricName;
	
	private List<Dimension> dimensions;
	
	private GetMetricStatisticsResult stats;
	
	
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNameSpace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	
	
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	
	public List<Dimension> getDimensions() {
		return dimensions;
	}
	
	public void setDimensions(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}
	
	
	public GetMetricStatisticsResult getStats() {
		return stats;
	}
	
	public void setStats(GetMetricStatisticsResult stats) {
		this.stats = stats;
	}

}
