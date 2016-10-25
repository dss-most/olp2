package th.go.dss.olp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import th.go.dss.olp.dao.OlpDao;

@RestController("/json")
public class JsonController {
	@Autowired
	private OlpDao olpDao;

	@RequestMapping(value="/json/listPlanActivity")
	public @ResponseBody List<Map<String, Object>> listPlanActivity(
			@RequestParam String fiscalYear
			) {
		return olpDao.findPlanActivitiesByFiscalYear(fiscalYear,true);
		
	}
	
	@RequestMapping(value="/json/listActivity")
	public @ResponseBody List<Map<String, Object>> listActivity(
			@RequestParam String fiscalYear,
			@RequestParam(required=false) Integer applicantId
			) {
		return olpDao.findActivitiesByFiscalYear(fiscalYear, applicantId);
		
	}
	
	@RequestMapping(value={"/json/listCustomer/{fiscalYear}", "/json/listCustomer"})
	public @ResponseBody List<Map<String, Object>> listCustomer(
			@RequestParam String customerCode,
			@PathVariable(required=false) Integer fiscalYear) {
		
		return olpDao.findApplicantByCustomerCode(customerCode, fiscalYear);
	}
	
	@RequestMapping(value="/json/listRegisterNumber/{fiscalYear}/")
	public @ResponseBody List<Map<String, Object>> listRegisterNumber (
			@PathVariable String fiscalYear,
			@RequestParam String query) {
		
		String firstString = "";
		
		if(fiscalYear != null && fiscalYear.length() > 2) {
			firstString = fiscalYear.substring(fiscalYear.length()-2, fiscalYear.length());
		} 
		
		return olpDao.findRegisterNumberByLeadingString(firstString, query);
	}
	
	@RequestMapping(value="/json/listRegisterByActivtiy")
	public @ResponseBody List<Map <String, Object>> listRegisterByActivity (
			@RequestParam String fiscalYear,
			@RequestParam Integer activityId){
		return olpDao.findRegistrationsByFiscalYearAndActivity(fiscalYear, activityId);
	}
	
	@RequestMapping(value="/json/listRegister") 
	public @ResponseBody  List<Map<String, Object>> listRegister (
			@RequestParam String fiscalYear,
			@RequestParam String firstRegisterNumber,
			@RequestParam String lastRegisterNumber,
			@RequestParam String activityType) {
		return olpDao.findRegisterNumber(fiscalYear, firstRegisterNumber, lastRegisterNumber, activityType); 
	
	}

}
