package th.go.dss.olp.reports;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JRScriptletException;

public class MoveActivityListBack extends JRDefaultScriptlet {

	@Override
	public void afterDetailEval() throws JRScriptletException {
		// TODO Auto-generated method stub
		super.afterDetailEval();
		
		try {
			((JRRewindableDataSource) this.getParameterValue("activityList")).moveFirst();
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
