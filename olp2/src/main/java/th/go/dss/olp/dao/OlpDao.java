package th.go.dss.olp.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface OlpDao {

	public List<Map <String, Object>> findActivitiesByFiscalYear(String  fiscalYear, Integer applicantId);
	
	public List<Map <String, Object>> findApplicantByCustomerCode(String  customerCode, Integer fiscalYear);
	
	public List<Map <String, Object>> findRegistrationsByFiscalYearAndListofActivities(String fiscalYear, Set<Integer> activityIdSet, Boolean allExceptActivities);
	
	public List<Map <String, Object>> findRegistrationsByFiscalYearAndCustomerCode(String fiscalYear, String customerCode, Set<Integer> activityIdSet);

	public List<Map<String, Object>> findRegisterNumberByLeadingString(String firstString, String lastString);

	public List<Map<String, Object>> findRegisterNumber(String fiscalYear,
			String firstRegisterNumber, String lastRegisterNumber, String activityType);

	public List<Map<String, Object>> findRegistrationsById(Set<Integer> registerIds, String activitySearch);

	public List<Map<String, Object>> findRegistrationsForEmsByIds(
			Set<Integer> registerIds);

	public List<Map<String, Object>> findPlanActivitiesByFiscalYear(String fiscalYear, Boolean formatDate);
	
	public List<Map<String, Object>> findRegistrationsByFiscalYearAndActivity(String fiscalYear, Integer activityId);
	
	public List<Map<String, Object>> findRegistrationsByFiscalYear(String fiscalYear);
	
	public List<Map<String, Object>> findCustomerByFiscalYear(String fiscalYear);


}
