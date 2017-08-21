package th.go.dss.olp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import th.go.dss.olp.dao.OlpDao;
import th.go.dss.olp.poi.AbstractPOIExcelView;
import th.go.dss.olp.poi.CustomerExport;
import th.go.dss.olp.poi.CustomerOnlyExport;
import th.go.dss.olp.poi.EMSExport;
import th.go.dss.olp.poi.PlanActivityExcelExport;
import th.go.dss.olp.reports.ThJasperReportsPdfView;

@Controller
public class HomeController {
	@Autowired
	private OlpDao olpDao;
	
	@Autowired 
	private ApplicationContext appContext;
	
	
	public static Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	
	@RequestMapping("/") 
	public String login(Model model) {
		
		return "home";
	}
	
	
	@RequestMapping("/reports/{reportName}") 
	public String report(@PathVariable String reportName) {
		
		return "reports/"+reportName;
	}
	
	@RequestMapping(value="/reports/exportAllCustomer")
	public ModelAndView exportAllCustomer(){
		
		List<Map<String, Object>> list = null;
		final Map<String, Object> model = new HashMap<>();
			
		list = olpDao.findCustomerByFiscalYear(null);
		
		model.put("customerList", list);
		
		AbstractPOIExcelView view = new CustomerOnlyExport();
		ModelAndView returnView = new ModelAndView(view,model);
		
		return returnView;
		
	}
	
	@RequestMapping(value="/reports/exportCustomerByFiscalYear")
	public ModelAndView exportCustomerByFiscalYear(
			@RequestParam(required=true) String fiscalYear){
		
		List<Map<String, Object>> list = null;
		final Map<String, Object> model = new HashMap<>();
		
	
		
			list = olpDao.findCustomerByFiscalYear(fiscalYear);
	
		
		model.put("customerList", list);
		model.put("fiscalYear", fiscalYear);
		
		AbstractPOIExcelView view = new CustomerOnlyExport();
		ModelAndView returnView = new ModelAndView(view,model);
		
		return returnView;
		
	}
	
	
	@RequestMapping(value="/reports/exportCustomerRegistration")
	public ModelAndView exportCustomerRegistration(
			@RequestParam(required=true) String fiscalYear,
			@RequestParam Integer activityId){
		
		List<Map<String, Object>> list = null;
		final Map<String, Object> model = new HashMap<>();
		
		if(activityId == null) {
			list = olpDao.findRegistrationsByFiscalYear(fiscalYear);
		} else {
		
			list = olpDao.findRegistrationsByFiscalYearAndActivity(fiscalYear, activityId);
		}
		
		model.put("registrationList", list);
		model.put("activityId", activityId);
		model.put("fiscalYear", fiscalYear);
		
		AbstractPOIExcelView view = new CustomerExport();
		ModelAndView returnView = new ModelAndView(view,model);
		
		return returnView;
		
	}
	
	
	@RequestMapping(value="/reports/reportPlanActivity")
	public ModelAndView reportPlanActivity(
			@RequestParam(required=true) String fiscalYear,
			@RequestParam(required=true) String reportPage
			) {
		
		List<Map<String, Object>> list = null;
		ModelAndView viewReturn = null;
		final Map<String, Object> model = new HashMap<>();
		
		if(reportPage.equals("excelReport")) {
			model.put("fiscalYear", fiscalYear);
			list = olpDao.findPlanActivitiesByFiscalYear(fiscalYear,true);
			
			model.put("planActivityList", list);
			
			AbstractPOIExcelView view = new PlanActivityExcelExport();
			viewReturn = new ModelAndView(view, model);
			
			
		} else if(reportPage.equals("pdfReport")) {
			
			model.put("fiscalYear", fiscalYear);
			logger.debug(fiscalYear);
			
			
			HashMap<String, Object> line = new HashMap<String,Object>();
			line.put("fiscalYear", fiscalYear);
			list = new ArrayList<>();
			list.add(line);
			
			model.put("datasource",list);
			
			List<Map<String, Object>> list2 = null;
			list2 = olpDao.findPlanActivitiesByFiscalYear(fiscalYear,false);
			JRRewindableDataSource activityList = new JRBeanCollectionDataSource(list2);
			
		    model.put("activityList", activityList);
		    
		    final JasperReportsPdfView view = new ThJasperReportsPdfView();
		    view.setUrl("classpath:reports/planActivityPdf.jrxml");
		    view.setApplicationContext(appContext);
		    view.setReportDataKey("activityList");
		    
		  
		    
			viewReturn = new ModelAndView(view, model);
		}
		
		logger.debug("returning view: " + viewReturn);
		
		return viewReturn;
	}
	
	@RequestMapping(value="/reports/pdfReportByRegisterIds")
	public ModelAndView pdfInvoiceByRegisterNumber(
			@RequestParam(required=true) Set<Integer> registerIds,
			@RequestParam(required=false) String reportPage,
			@RequestParam(required=false) Boolean chkbx_englishReport,
			@RequestParam(required=true) String fiscalYear,
			@RequestParam(required=true) String activityType
			) {
		
		logger.debug("Entering /reports/pdfReportByRegisterIds with reportPage: " + reportPage);
		
		
		final Map<String, Object> model = new HashMap<>();
		
		List<Map<String, Object>> list = null;
		ModelAndView returnView = null;
		model.put("fiscalYear", fiscalYear);
		
		
		if("excelEMSExport".equals(reportPage)) {
			list = olpDao.findRegistrationsForEmsByIds(registerIds);
			model.put("registrationList", list);
			
			AbstractPOIExcelView view = new EMSExport();
			returnView = new ModelAndView(view,model);
			
			return returnView;
			
		} else {
			String activitySearch = " LIKE '%' ";
			
			if(activityType.equals("qc") ){
				activitySearch = " LIKE 'QC%' ";
			} else if(activityType.equals("act")) {
				activitySearch = " NOT LIKE 'QC%' ";
			}
			
			list = olpDao.findRegistrationsById(registerIds, activitySearch);
			
			logger.debug("list.size(): " + list.size());
			
			
		}
		
		for(Map<String, Object> map : list) {
			String actitivityCode = (String) map.get("ACTIVITY_CODE");
			
			if(actitivityCode != null ) {
			
				if(actitivityCode.startsWith("QC")) {
					map.put("IS_QC_ACTIVITY", true);
				} else {
					map.put("IS_QC_ACTIVITY", false);
				}
			}
		}
		
		if(list!= null && list.size() > 0 ) {
			
			if(chkbx_englishReport!=null && chkbx_englishReport == true) {
				model.put("isEnglishAddress", true);
			} else {
				model.put("isEnglishAddress", false);
			}
			
			model.put("fields", list);
		}
		
		if("quotationReport".equals(reportPage)) {
			
			JasperReportsPdfView reportView = new ThJasperReportsPdfView();
			reportView.setUrl("classpath:reports/invoice.jrxml");
			reportView.setApplicationContext(appContext);
			reportView.setReportDataKey("fields");
			
			returnView = new ModelAndView(reportView,model);
		} else {
			JasperReportsPdfView reportView = new ThJasperReportsPdfView();
			reportView.setUrl("classpath:reports/"+reportPage+".jrxml");
			reportView.setApplicationContext(appContext);
			reportView.setReportDataKey("fields");
			
			returnView = new ModelAndView(reportView,model);
		}
		
		return returnView;
	}
	
	@RequestMapping(value="reports/printInvoice") 
	public ModelAndView printInvoice(
			@RequestParam(required=false) String fiscalYear,
			@RequestParam(required=false) String customerCode,
			@RequestParam(required=false) String activityId,
			@RequestParam(required=false) String allExceptActivityFlag,
			@RequestParam(required=false) String reportPage,
			@RequestParam(required=false) Boolean chkbx_englishReport,
			@RequestParam(required=false) String activityType) {
		
		logger.debug("reportPage: " + reportPage);
		logger.debug("activityId: " + activityId);
		logger.debug("customerCode: " + customerCode);
		logger.debug("chkbx_englishReport: " + chkbx_englishReport);
		
		final Map<String, Object> model = new HashMap<>();
		
		
		
		Set<Integer> activityIdSet = new HashSet<Integer>();
		if(activityId != null) {
			StringTokenizer token = new StringTokenizer(activityId,",");
			while(token.hasMoreTokens()) {
				Integer id = Integer.parseInt((String) token.nextElement());
				activityIdSet.add(id);
			}
			
		}
		
		List<Map<String, Object>> list = null;
		
		logger.debug("reportPage");
		
		
		if("quotationReport".equals(reportPage)) {
		
		
			if(customerCode == null || customerCode.length() <= 0) {
				Boolean allExceptActivity = false;
				if(allExceptActivityFlag != null && allExceptActivityFlag.length() > 0 ) {
					allExceptActivity = true;
				}
				logger.debug("allExceptActivity: " + allExceptActivity);
				list = olpDao.findRegistrationsByFiscalYearAndListofActivities(fiscalYear, activityIdSet, allExceptActivity);
			} else {
				list = olpDao.findRegistrationsByFiscalYearAndCustomerCode(fiscalYear, customerCode, activityIdSet);
			}
			
			
			if(list!= null && list.size() > 0 ) {
				
				model.put("fiscalYear", fiscalYear);
				
				if(chkbx_englishReport!=null && chkbx_englishReport == true) {
					model.put("isEnglishAddress", true);
				} else {
					model.put("isEnglishAddress", false);
				}
				
				
				
				JRRewindableDataSource activityList = new JRBeanCollectionDataSource(list);

				
				model.put("fields", activityList);
				
				
				JasperReportsPdfView view = new ThJasperReportsPdfView();
				view.setUrl("classpath:reports/invoice.jrxml");
				view.setApplicationContext(appContext);
				view.setReportDataKey("fields");
				
				logger.debug("url: " + view.getUrl());
				return new ModelAndView(view, model);
				
			} 
			
		} else if("confirmReport".equals(reportPage)) {

			if(customerCode == null || customerCode.length() <= 0) {
				list = null;
			} else {
				list = olpDao.findRegistrationsByFiscalYearAndCustomerCode(fiscalYear, customerCode, activityIdSet);
			}
			
			
			if(list!= null && list.size() > 0 ) {
				
				model.put("fiscalYear", fiscalYear);
				model.put("fields", list);
				

				JasperReportsPdfView view = new ThJasperReportsPdfView();
				view.setUrl("classpath:reports/confirm_letter.jrxml");
				view.setApplicationContext(appContext);
				
				return new ModelAndView(view,model);
				
			} 

		} else if("confirm2Report".equals(reportPage)) {

			if(customerCode == null || customerCode.length() <= 0) {
				Boolean allExceptActivity = false;
				if(allExceptActivityFlag != null && allExceptActivityFlag.length() > 0 ) {
					allExceptActivity = true;
				}
				logger.debug("allExceptActivity: " + allExceptActivity);
				list = olpDao.findRegistrationsByFiscalYearAndListofActivities(fiscalYear, activityIdSet, allExceptActivity);
			} else {
				list = olpDao.findRegistrationsByFiscalYearAndCustomerCode(fiscalYear, customerCode, activityIdSet);
			}
			
			for(Map<String, Object> map : list) {
				String actitivityCode = (String) map.get("ACTIVITY_CODE");
				if(actitivityCode.startsWith("QC")) {
					map.put("IS_QC_ACTIVITY", true);
				} else {
					map.put("IS_QC_ACTIVITY", false);
				}
			}
			
			
			if(list!= null && list.size() > 0 ) {
				
				model.put("fiscalYear", fiscalYear);
				
				
				if(chkbx_englishReport!=null && chkbx_englishReport == true) {
					model.put("isEnglishAddress", true);
				} else {
					model.put("isEnglishAddress", false);
				}
				
				Map<String, Object> test = list.get(0);
				
				model.put("fields", list);


				JasperReportsPdfView view = new ThJasperReportsPdfView();
				view.setUrl("classpath:reports/confirm2_letter.jrxml");
				view.setApplicationContext(appContext);
				
				return new ModelAndView(view,model);
				
			} 

		}
		
		return null;

	}
	
}
