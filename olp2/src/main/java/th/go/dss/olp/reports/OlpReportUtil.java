package th.go.dss.olp.reports;

import java.math.BigDecimal;

public class OlpReportUtil {
	public static String activityStr(String exampleName, String activityName, BigDecimal round) {
		String s;
		
		s=exampleName + ": ";
		
		if(activityName.contains(", Range")) {
			Integer rangeIndex = activityName.indexOf(", Range");
			s = s + activityName.substring(0, rangeIndex);
		} else {
			s = s + activityName;
		}
		
		if(round != null) {
			s = s+ " (round " + round + ")";
		}
		
		return s;
		
	}
}
