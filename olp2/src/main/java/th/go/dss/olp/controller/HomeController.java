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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import th.go.dss.olp.dao.OlpDao;
import th.go.dss.olp.poi.AbstractPOIExcelView;
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
	
	
	@RequestMapping("/reports/r01") 
	public String reportR01(Model model) {
		
		return "reports/r01";
	}
	
	@RequestMapping("/reports/r02") 
	public String reportR02(Model model) {
		
		return "reports/r02";
	}
	
	@RequestMapping("/reports/r03") 
	public String reportR03(Model model) {
		
		return "reports/r03";
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
	public String pdfInvoiceByRegisterNumber(
			@RequestParam(required=true) Set<Integer> registerIds,
			@RequestParam(required=false) String reportPage,
			@RequestParam(required=false) Boolean chkbx_englishReport,
			@RequestParam(required=true) String fiscalYear,
			@RequestParam(required=true) String activityType,
			Model model) {
		
		List<Map<String, Object>> list = null;
		
		
		if("excelEMSExport".equals(reportPage)) {
			list = olpDao.findRegistrationsForEmsByIds(registerIds);
			model.addAttribute("registrationList", list);
		} else {
			String activitySearch = " LIKE '%' ";
			
			if(activityType.equals("qc") ){
				activitySearch = " LIKE 'QC%' ";
			} else if(activityType.equals("act")) {
				activitySearch = " NOT LIKE 'QC%' ";
			}
			
			list = olpDao.findRegistrationsById(registerIds, activitySearch);
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
			
			model.addAttribute("fiscalYear", fiscalYear);
			
			if(chkbx_englishReport!=null && chkbx_englishReport == true) {
				model.addAttribute("isEnglishAddress", true);
			} else {
				model.addAttribute("isEnglishAddress", false);
			}
			
			model.addAttribute("feilds", list);
		}
		
		if("quotationReport".equals(reportPage)) {
			return "testReport";
		}
		
		return reportPage;
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
				
				logger.debug(test.get("ADD_RECEIPT").toString());
				
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
