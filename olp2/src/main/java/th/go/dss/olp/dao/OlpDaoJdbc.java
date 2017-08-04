package th.go.dss.olp.dao;



import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OlpDaoJdbc implements OlpDao {
	

	
	private static final Logger logger = LoggerFactory.getLogger(OlpDaoJdbc.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired() 
	@Qualifier("dataSource")
	public void setDataSource(DataSource ds) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
	}

	private RowMapper<Map<String, Object>> genericRowMapperForJS = new RowMapper<Map<String, Object>>() {
		public Map<String, Object> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
				Map<String, Object> map = new HashMap <String, Object>();
				
				// now get all the column
				ResultSetMetaData rsmd  = rs.getMetaData();
				Integer columnCount = rsmd.getColumnCount();
				for(Integer i=0; i<columnCount;  i++) {
					//logger.debug("geting columnName: " + rsmd.getColumnName(i+1));
					//logger.debug("geting value: " + rs.getObject(i+1));
					
					Object result;
					if(rsmd.getColumnType(i+1) == java.sql.Types.DATE || 
							rsmd.getColumnType(i+1) == java.sql.Types.TIMESTAMP	) {
						Date date = rs.getDate(i+1);
						
						if(date != null) {
							result = sdf.format(date);
						} else {
							result = null;
						}
					} else {
						result = rs.getObject(i+1);
					}
					
					
					map.put(rsmd.getColumnName(i+1),result);
				}
			return map;
		}
	};
	
	private RowMapper<Map<String, Object>> genericRowMapper = new RowMapper<Map<String, Object>>() {
		public Map<String, Object> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
				Map<String, Object> map = new HashMap <String, Object>();
				
				// now get all the column
				ResultSetMetaData rsmd  = rs.getMetaData();
				Integer columnCount = rsmd.getColumnCount();
				for(Integer i=0; i<columnCount;  i++) {
					//logger.debug("geting columnName: " + rsmd.getColumnName(i+1));
					//logger.debug("geting value: " + rs.getObject(i+1));
					
					Object result;
					result = rs.getObject(i+1);
					
					map.put(rsmd.getColumnName(i+1),result);
				}
			return map;
		}
	};
	
	

	@Override
	public List<Map<String, Object>> findPlanActivitiesByFiscalYear(String fiscalYear, Boolean formatDate) {
		String sql1 = "" +
				"select p.id PLAN_ID, p.PLAN_CODE, p.branch_name, e.id EXAMPLE_ID, e.EXAMPLE_CODE, e.EXAMPLE_NAME, " +
				"	a.id ACTIVITY_ID, a.ACTIVITY_CODE, " +
				"	a.ACTIVITY_NAME, a.price, a.ROOM_NUMBER, a.CLOSE_APPLICANT_DATE, a.START_ACTIVITY_DATE, " +
				"   a.ROUND, emp.EMP_NAME, a.BARCODE " +
				"from OLP_EXAMPLE e, olp_plan p, OLP_ACTIVITY a, HR_EMPLOYEE emp " +
				"where e.OLP_PLAN_ID = p.id " +
				"	and a.olp_example_id = e.ID" +
				" 	and a.emp_emp_id = emp.emp_id(+) " +
				"  	and p.FISCAL_YEAR=:fiscalYear " +
				"order by p.id asc, e.id asc, a.id asc";
				
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		
		List<Map<String, Object>> returnList;
		if(formatDate)
		
			 returnList = this.jdbcTemplate.query(
					sql1,
					params,
					genericRowMapperForJS
					);
		else {
			returnList = this.jdbcTemplate.query(
					sql1,
					params,
					genericRowMapper
					);
		}
		
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findActivitiesByFiscalYear(
			String fiscalYear, Integer applicantId) {
		String sql1 = "" +
				"select act.id, act.activity_code, act.activity_name, " +
				"	 pln.branch_name, exm.example_name " +
				"from olp_activity act, olp_plan pln , olp_example exm "  +
				"where act.olp_example_id = exm.id " +
				"	and exm.olp_plan_id = pln.id " +
				"	and pln.fiscal_year= :fiscalYear " + 
				"order by act.activity_code asc";

		String sql2 = "" +
				"select act.id, act.activity_code, act.activity_name, " +
				"	 pln.branch_name, exm.example_name, reg.register_number " +
				"from olp_activity act, olp_plan pln , olp_example exm," +
				"	olp_register reg, olp_register_activity rac, olp_applicant app " +
				"where act.olp_example_id = exm.id " +
				"	and exm.olp_plan_id = pln.id " +
				"	and reg.id = rac.register_id " +
				"	and app.id = reg.applicant_id " +
				"	and act.id = rac.activity_id " +
				"	and app.id = :applicantId" +
				"	and reg.fiscal_year = :fiscalYear " +
				"	and rac.STATUS_CANCLE_ACTIVITY is null " +
				"order by reg.id asc, rac.id asc";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		
		String sql;
		if(applicantId == null ){
			logger.debug("sql1: fiscalYear = " + fiscalYear);
			sql = sql1;
		} else {
			logger.debug("sql2: fiscalYear = " + fiscalYear);
			logger.debug("      applicantId = " + applicantId);
			sql = sql2;
			params.put("applicantId", applicantId);
		}
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql,
				params,
				genericRowMapper
				);
		
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findCustomerByFiscalYear(String fiscalYear) {
		// TODO Auto-generated method stub
		String sql1 = "" +
				"select ap.customer_code " +
				"	, prefix_app.PFIX_NAME " +
				"	, ap.customer_name_candidate " +
				"	, ap.SEND_MISSIVE_NAME " +
				"	, ap.position_candidate " +
				"	, ap.tel_no " +
				"	, ap.fax_no " +
				"	, ap.phon_no " +
				"	, ap.email " +
				"	, olp_get_act_from_applicant_id(ap.id, " +fiscalYear +") activities " +
				
				
				" 	, c.company_th_applicant " +
				" 	, c.company_en_applicant " +
				" 	, c.add_applicant " +
				" 	, c.add_applicant_1 " +
				" 	, c.add_en_applicant " +
				" 	, c.add_en_applicant_1 " +
				
				"	, decode(nvl(c.tambon_id_applicant,\'0\'), \'0\', \' \', " +
				"  			decode(p.province_id, 21 , \'แขวง\'||t.tambon_name, " +
				"  					\'ต.\'||t.tambon_name )) tambon_name " +
				"	, decode(p.province_id, 21 , \'เขต\'||d.amphur_name, " +
				"			\'อ.\'||d.amphur_name ) amphur_name " +
				"	, decode(p.province_id, 21 , p.province_name, "+
				"			\'จ.\'||p.province_name ) province_name " +
				
				"	, c.postcode_applicant " +
				
				" 	, c.company_th_receipt " +
				" 	, c.company_en_receipt " +
				" 	, c.add_receipt " +
				" 	, c.add_receipt_1 " +
				" 	, c.add_en_receipt " +
				" 	, c.add_en_receipt_1 " +
				"	, c.org_Type " +
				
				"	, decode(nvl(c.tambon_id_receipt,\'0\'), \'0\', \' \', " +
				"  			decode(p_receipt.province_id, 21 , \'แขวง\'||t_receipt.tambon_name, " +
				"  					\'ต.\'||t_receipt.tambon_name )) tambon_name_1 " +
				"	, decode(p_receipt.province_id, 21 , \'เขต\'||d_receipt.amphur_name, " +
				"			\'อ.\'||d_receipt.amphur_name ) amphur_name_1 " +
				"	, decode(p_receipt.province_id, 21 , p_receipt.province_name, "+
				"			\'จ.\'||p_receipt.province_name ) province_name_1 " +

				"	, c.postcode_receipt " +
				
				
				" 	, c.company_th_receipt2 " +
				" 	, c.company_en_receipt2 " +
				" 	, c.add_receipt2 " +
				" 	, c.add_receipt2_1 " +
				" 	, c.add_en_receipt2 " +
				" 	, c.add_en_receipt2_1 " +

				
				"	, decode(nvl(c.tambon_id_receipt2,\'0\'), \'0\', \' \', " +
				"  			decode(p_receipt2.province_id, 21 , \'แขวง\'||t_receipt2.tambon_name, " +
				"  					\'ต.\'||t_receipt2.tambon_name )) tambon_name_2 " +
				"	, decode(p_receipt2.province_id, 21 , \'เขต\'||d_receipt2.amphur_name, " +
				"			\'อ.\'||d_receipt2.amphur_name ) amphur_name_2 " +
				"	, decode(p_receipt2.province_id, 21 , p_receipt2.province_name, "+
				"			\'จ.\'||p_receipt2.province_name ) province_name_2 " +
				"	, c.postcode_receipt2 " +
				
				" 	, c.company_th_certificate " +
				" 	, c.company_en_certificate " +
				" 	, c.add_certificate " +
				" 	, c.add_certificate_1 " +
				" 	, c.add_en_certificate " +
				" 	, c.add_en_certificate_1 " +
				"	, decode(nvl(c.tambon_id_certificate,\'0\'), \'0\', \' \', " +
				"  			decode(p_cer.province_id, 21 , \'แขวง\'||t_cer.tambon_name, " +
				"  					\'ต.\'||t_receipt2.tambon_name )) tambon_name_3 " +
				"	, decode(p_cer.province_id, 21 , \'เขต\'||d_cer.amphur_name, " +
				"			\'อ.\'||d_cer.amphur_name ) amphur_name_3 " +
				"	, decode(p_cer.province_id, 21 , p_receipt2.province_name, "+
				"			\'จ.\'||p_cer.province_name ) province_name_3 " +
				"	, c.postcode_certificate " +
				
				
				
				"from " +
				"	olp_applicant ap inner join olp_company c on ap.olp_ref_company_id = c.id " +
				
				" left outer join hr_prefix prefix_app on ap.PFIX_ID_CANDIDATE = prefix_app.PFIX_ID " +
				
				"	left outer join glb_province p on c.province_id_applicant = p.province_id " +
				"	left outer join glb_district d on c.district_id_applicant = d.amphur_id " +
				"	left outer join glb_tambon t on c.tambon_id_applicant = t.tambon_id " +
				
				
				"	left outer join glb_province p_receipt on c.province_id_receipt = p_receipt.province_id " +
				"	left outer join glb_district d_receipt on c.district_id_receipt = d_receipt.amphur_id " +
				"	left outer join glb_tambon t_receipt on c.tambon_id_receipt = t_receipt.tambon_id " +
				
				"	left outer join glb_province p_receipt2 on c.province_id_receipt2 = p_receipt2.province_id " +
				"	left outer join glb_district d_receipt2 on c.district_id_receipt2 = d_receipt2.amphur_id " +
				"	left outer join glb_tambon t_receipt2 on c.tambon_id_receipt2 = t_receipt2.tambon_id " +
				
				"	left outer join glb_province p_cer on c.province_id_certificate = p_cer.province_id " +
				"	left outer join glb_district d_cer on c.district_id_certificate = d_cer.amphur_id " +
				"	left outer join glb_tambon t_cer on c.tambon_id_certificate = t_cer.tambon_id " +



				
				"where ap.id in ( " +
				"	select r.applicant_id from olp_register r where r.fiscal_year = :fiscalYear  and r.status_regis_form != 'ST13' ) " +
//				"	AND ra.STATUS_CANCLE_ACTIVITY is null " +	
				"order by ap.customer_code asc";
		
		
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		
		
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql1,
				params,
				genericRowMapper
				);
		
		logger.debug(sql1);
		logger.debug("fiscalYear: " + fiscalYear);
		logger.debug("returnList.size() : " + returnList.size());
		
		return returnList;	
	
	}
	
	
	@Override
	public List<Map<String, Object>> findRegistrationsByFiscalYear(String fiscalYear) {
		// TODO Auto-generated method stub
		String sql1 = "" +
				"select r.register_number " +
				"	, r.end_payment_date " +
				"	, r.status_regis_form " +
				"	, ap.customer_code " +
				"	, prefix_app.PFIX_NAME " +
				"	, ap.customer_name_candidate " +
				"	, ap.SEND_MISSIVE_NAME " +
				"	, ap.position_candidate " +
				"	, ap.tel_no " +
				"	, ap.fax_no " +
				"	, ap.phon_no " +
				"	, ap.email " +
				"	, olp_get_act_from_reg_id(r.id) activities " +
				
				
				" 	, c.company_th_applicant " +
				" 	, c.company_en_applicant " +
				" 	, c.add_applicant " +
				" 	, c.add_applicant_1 " +
				" 	, c.add_en_applicant " +
				" 	, c.add_en_applicant_1 " +
				
				"	, decode(nvl(c.tambon_id_applicant,\'0\'), \'0\', \' \', " +
				"  			decode(p.province_id, 21 , \'แขวง\'||t.tambon_name, " +
				"  					\'ต.\'||t.tambon_name )) tambon_name " +
				"	, decode(p.province_id, 21 , \'เขต\'||d.amphur_name, " +
				"			\'อ.\'||d.amphur_name ) amphur_name " +
				"	, decode(p.province_id, 21 , p.province_name, "+
				"			\'จ.\'||p.province_name ) province_name " +
				
				"	, c.postcode_applicant " +
				
				" 	, c.company_th_receipt " +
				" 	, c.company_en_receipt " +
				" 	, c.add_receipt " +
				" 	, c.add_receipt_1 " +
				" 	, c.add_en_receipt " +
				" 	, c.add_en_receipt_1 " +
				"	, c.org_Type " +
				
				"	, decode(nvl(c.tambon_id_receipt,\'0\'), \'0\', \' \', " +
				"  			decode(p_receipt.province_id, 21 , \'แขวง\'||t_receipt.tambon_name, " +
				"  					\'ต.\'||t_receipt.tambon_name )) tambon_name_1 " +
				"	, decode(p_receipt.province_id, 21 , \'เขต\'||d_receipt.amphur_name, " +
				"			\'อ.\'||d_receipt.amphur_name ) amphur_name_1 " +
				"	, decode(p_receipt.province_id, 21 , p_receipt.province_name, "+
				"			\'จ.\'||p_receipt.province_name ) province_name_1 " +

				"	, c.postcode_receipt " +
				
				
				" 	, c.company_th_receipt2 " +
				" 	, c.company_en_receipt2 " +
				" 	, c.add_receipt2 " +
				" 	, c.add_receipt2_1 " +
				" 	, c.add_en_receipt2 " +
				" 	, c.add_en_receipt2_1 " +

				
				"	, decode(nvl(c.tambon_id_receipt2,\'0\'), \'0\', \' \', " +
				"  			decode(p_receipt2.province_id, 21 , \'แขวง\'||t_receipt2.tambon_name, " +
				"  					\'ต.\'||t_receipt2.tambon_name )) tambon_name_2 " +
				"	, decode(p_receipt2.province_id, 21 , \'เขต\'||d_receipt2.amphur_name, " +
				"			\'อ.\'||d_receipt2.amphur_name ) amphur_name_2 " +
				"	, decode(p_receipt2.province_id, 21 , p_receipt2.province_name, "+
				"			\'จ.\'||p_receipt2.province_name ) province_name_2 " +
				"	, c.postcode_receipt2 " +
				
				" 	, c.company_th_certificate " +
				" 	, c.company_en_certificate " +
				" 	, c.add_certificate " +
				" 	, c.add_certificate_1 " +
				" 	, c.add_en_certificate " +
				" 	, c.add_en_certificate_1 " +
				"	, decode(nvl(c.tambon_id_certificate,\'0\'), \'0\', \' \', " +
				"  			decode(p_cer.province_id, 21 , \'แขวง\'||t_cer.tambon_name, " +
				"  					\'ต.\'||t_receipt2.tambon_name )) tambon_name_3 " +
				"	, decode(p_cer.province_id, 21 , \'เขต\'||d_cer.amphur_name, " +
				"			\'อ.\'||d_cer.amphur_name ) amphur_name_3 " +
				"	, decode(p_cer.province_id, 21 , p_receipt2.province_name, "+
				"			\'จ.\'||p_cer.province_name ) province_name_3 " +
				"	, c.postcode_certificate " +
				
				
				
				"from " +
				"	olp_register r inner join olp_applicant ap on r.applicant_id = ap.id " +
				"	inner join olp_company c on ap.olp_ref_company_id = c.id " +
				
				" left outer join hr_prefix prefix_app on ap.PFIX_ID_CANDIDATE = prefix_app.PFIX_ID " +
				
				"	left outer join glb_province p on c.province_id_applicant = p.province_id " +
				"	left outer join glb_district d on c.district_id_applicant = d.amphur_id " +
				"	left outer join glb_tambon t on c.tambon_id_applicant = t.tambon_id " +
				
				
				"	left outer join glb_province p_receipt on c.province_id_receipt = p_receipt.province_id " +
				"	left outer join glb_district d_receipt on c.district_id_receipt = d_receipt.amphur_id " +
				"	left outer join glb_tambon t_receipt on c.tambon_id_receipt = t_receipt.tambon_id " +
				
				"	left outer join glb_province p_receipt2 on c.province_id_receipt2 = p_receipt2.province_id " +
				"	left outer join glb_district d_receipt2 on c.district_id_receipt2 = d_receipt2.amphur_id " +
				"	left outer join glb_tambon t_receipt2 on c.tambon_id_receipt2 = t_receipt2.tambon_id " +
				
				"	left outer join glb_province p_cer on c.province_id_certificate = p_cer.province_id " +
				"	left outer join glb_district d_cer on c.district_id_certificate = d_cer.amphur_id " +
				"	left outer join glb_tambon t_cer on c.tambon_id_certificate = t_cer.tambon_id " +



				
				"where " +
				"	r.fiscal_year = :fiscalYear  and r.status_regis_form != 'ST13' " +
//				"	AND ra.STATUS_CANCLE_ACTIVITY is null " +	
				"order by ap.customer_code asc, r.register_number asc";
		
		
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		
		
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql1,
				params,
				genericRowMapper
				);
		
		logger.debug(sql1);
		logger.debug("fiscalYear: " + fiscalYear);
		logger.debug("returnList.size() : " + returnList.size());
		
		return returnList;	
	
	}
	
	
	
	@Override
	public List<Map<String, Object>> findRegistrationsByFiscalYearAndActivity(String fiscalYear, Integer activityId) {
		// TODO Auto-generated method stub
		String sql1 = "" +
				"select r.register_number " +
				"	, r.end_payment_date " +
				"	, r.status_regis_form " +
				"	, ra.c_password " +
				"	, a.activity_name " +
				"	, a.activity_code " +
				"	, ap.customer_code " +
				"	, prefix_app.PFIX_NAME " +
				"	, ap.customer_name_candidate " +
				"	, ap.position_candidate " +
				"	, ap.tel_no " +
				"	, ap.fax_no " +
				"	, ap.phon_no " +
				"	, ap.email " +
				"	, ap.SEND_MISSIVE_NAME " +
				"	, c.org_Type " +
				
				
				
				"	, a.activity_code " +
				"	, ex.example_name " +
				"	, pln.branch_name " +
				"	, a.activity_name " +
				"	, ra.exam_num "	+ 
				"	, ra.amount ra_amount" +
				"	, a.price " +
				
				" 	, c.company_th_applicant " +
				" 	, c.company_en_applicant " +
				" 	, c.add_applicant " +
				" 	, c.add_applicant_1 " +
				" 	, c.add_en_applicant " +
				" 	, c.add_en_applicant_1 " +
				"	, decode(nvl(c.tambon_id_applicant,\'0\'), \'0\', \' \', " +
				"  			decode(p.province_id, 21 , \'แขวง\'||t.tambon_name, " +
				"  					\'ต.\'||t.tambon_name )) tambon_name " +
				"	, decode(p.province_id, 21 , \'เขต\'||d.amphur_name, " +
				"			\'อ.\'||d.amphur_name ) amphur_name " +
				"	, decode(p.province_id, 21 , p.province_name, "+
				"			\'จ.\'||p.province_name ) province_name " +
				"	, c.postcode_applicant " +
				
				" 	, c.company_th_receipt " +
				" 	, c.company_en_receipt " +
				" 	, c.add_receipt " +
				" 	, c.add_receipt_1 " +
				" 	, c.add_en_receipt " +
				" 	, c.add_en_receipt_1 " +
				"	, decode(nvl(c.tambon_id_receipt,\'0\'), \'0\', \' \', " +
				"  			decode(p_receipt.province_id, 21 , \'แขวง\'||t_receipt.tambon_name, " +
				"  					\'ต.\'||t_receipt.tambon_name )) tambon_name_1 " +
				"	, decode(p_receipt.province_id, 21 , \'เขต\'||d_receipt.amphur_name, " +
				"			\'อ.\'||d_receipt.amphur_name ) amphur_name_1 " +
				"	, decode(p_receipt.province_id, 21 , p_receipt.province_name, "+
				"			\'จ.\'||p_receipt.province_name ) province_name_1 " +

				"	, c.postcode_receipt " +
				
				
				" 	, c.company_th_receipt2 " +
				" 	, c.company_en_receipt2 " +
				" 	, c.add_receipt2 " +
				" 	, c.add_receipt2_1 " +
				" 	, c.add_en_receipt2 " +
				" 	, c.add_en_receipt2_1 " +
				"	, decode(nvl(c.tambon_id_receipt2,\'0\'), \'0\', \' \', " +
				"  			decode(p_receipt2.province_id, 21 , \'แขวง\'||t_receipt2.tambon_name, " +
				"  					\'ต.\'||t_receipt2.tambon_name )) tambon_name_2 " +
				"	, decode(p_receipt2.province_id, 21 , \'เขต\'||d_receipt2.amphur_name, " +
				"			\'อ.\'||d_receipt2.amphur_name ) amphur_name_2 " +
				"	, decode(p_receipt2.province_id, 21 , p_receipt2.province_name, "+
				"			\'จ.\'||p_receipt2.province_name ) province_name_2 " +
				"	, c.postcode_receipt2 " +
				
				" 	, c.company_th_certificate " +
				" 	, c.company_en_certificate " +
				" 	, c.add_certificate " +
				" 	, c.add_certificate_1 " +
				" 	, c.add_en_certificate " +
				" 	, c.add_en_certificate_1 " +
				"	, decode(nvl(c.tambon_id_certificate,\'0\'), \'0\', \' \', " +
				"  			decode(p_cer.province_id, 21 , \'แขวง\'||t_cer.tambon_name, " +
				"  					\'ต.\'||t_receipt2.tambon_name )) tambon_name_3 " +
				"	, decode(p_cer.province_id, 21 , \'เขต\'||d_cer.amphur_name, " +
				"			\'อ.\'||d_cer.amphur_name ) amphur_name_3 " +
				"	, decode(p_cer.province_id, 21 , p_receipt2.province_name, "+
				"			\'จ.\'||p_cer.province_name ) province_name_3 " +
				"	, c.postcode_certificate " +
				
				
				
				"from " +
				"	olp_register r inner join olp_applicant ap on r.applicant_id = ap.id " +
				"	inner join olp_register_activity ra on ra.register_id = r.id " +  
				"	inner join olp_activity a on ra.activity_id = a.id " +
				" 	inner join olp_example ex on a.olp_example_id = ex.id " +
				"	inner join olp_plan pln on ex.olp_plan_id = pln.id " +
				"	inner join olp_company c on ap.olp_ref_company_id = c.id " +
				
				" left outer join hr_prefix prefix_app on ap.PFIX_ID_CANDIDATE = prefix_app.PFIX_ID " +
				
				"	left outer join glb_province p on c.province_id_applicant = p.province_id " +
				"	left outer join glb_district d on c.district_id_applicant = d.amphur_id " +
				"	left outer join glb_tambon t on c.tambon_id_applicant = t.tambon_id " +
				
				
				"	left outer join glb_province p_receipt on c.province_id_receipt = p_receipt.province_id " +
				"	left outer join glb_district d_receipt on c.district_id_receipt = d_receipt.amphur_id " +
				"	left outer join glb_tambon t_receipt on c.tambon_id_receipt = t_receipt.tambon_id " +
				
				"	left outer join glb_province p_receipt2 on c.province_id_receipt2 = p_receipt2.province_id " +
				"	left outer join glb_district d_receipt2 on c.district_id_receipt2 = d_receipt2.amphur_id " +
				"	left outer join glb_tambon t_receipt2 on c.tambon_id_receipt2 = t_receipt2.tambon_id " +
				
				"	left outer join glb_province p_cer on c.province_id_certificate = p_cer.province_id " +
				"	left outer join glb_district d_cer on c.district_id_certificate = d_cer.amphur_id " +
				"	left outer join glb_tambon t_cer on c.tambon_id_certificate = t_cer.tambon_id " +



				
				"where " +
				"	r.fiscal_year = :fiscalYear " +
				" 	AND ra.activity_id  = :activityId  " 	+
				"	AND ra.STATUS_CANCLE_ACTIVITY is null and r.status_regis_form != 'ST13' " +	
				"order by  ap.customer_code asc, r.register_number asc,  pln.id asc, ex.id asc ,a.id asc, ra.id asc";
		
		
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		params.put("activityId", activityId);
		
		
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql1,
				params,
				genericRowMapper
				);
		
		logger.debug(sql1);
		logger.debug("fiscalYear: " + fiscalYear);
		logger.debug("activityId: " + activityId);
		logger.debug("returnList.size() : " + returnList.size());
		
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findRegistrationsByFiscalYearAndListofActivities(
			String fiscalYear, Set<Integer> activityIdSet, Boolean allExceptActivities) {
		
		/**
		 * select r.register_number, ap.customer_code, ap.customer_name_candidate, r.id, ap.id, a.id
from olp_register r inner join olp_applicant ap on r.applicant_id = ap.id
  inner join olp_register_activity ra on ra.register_id = r.id 
  inner join olp_activity a on ra.activity_id = a.id
where a.activity_code like 'D01' and r.fiscal_year='2555'
;
		 */
		
		String sql1 = "" +
				"select r.register_number " +
				"	, r.end_payment_date " +
				"	, ap.customer_code " +
				"	, ap.customer_name_candidate " +
				"	, a.activity_code " +
				"	, ex.example_name " +
				"	, pln.branch_name " +
				"	, a.activity_name " +
				"	, ra.exam_num "	+ 
				"	, ra.amount ra_amount" +
				"	, a.price " +
				"	, c.company_th_receipt" +
				"	, c.add_receipt " +
				"	, decode(nvl(c.tambon_id_receipt,\'0\'), \'0\', \' \', " +
                "  			decode(p.province_id, 21 , \'แขวง\'||t.tambon_name, " +
                "  					\'ตำบล\'||t.tambon_name )) tambon " +
                "	, decode(p.province_id, 21 , \'เขต\'||d.amphur_name, " +
				"			\'อำเภอ\'||d.amphur_name ) amphur " +
				"	, decode(p.province_id, 21 , p.province_name, "+
				"			\'จังหวัด\'||p.province_name ) province " +
				"	, c.postcode_receipt postcode " +
				"from " +
				"	olp_register r inner join olp_applicant ap on r.applicant_id = ap.id " +
				"	inner join olp_register_activity ra on ra.register_id = r.id " +  
				"	inner join olp_activity a on ra.activity_id = a.id " +
				" 	inner join olp_example ex on a.olp_example_id = ex.id " +
				"	inner join olp_plan pln on ex.olp_plan_id = pln.id " +
				"	inner join olp_company c on ap.olp_ref_company_id = c.id " +
				"	inner join glb_province p on c.province_id_receipt = p.province_id " +
				"	inner join glb_district d on c.district_id_receipt = d.amphur_id " +
				"	left outer join glb_tambon t on c.tambon_id_receipt = t.tambon_id ";
		
		String where1 = "" +
				"where " +
				"	r.fiscal_year = :fiscalYear " +
				" 	AND r.id in (" +
				"		SELECT in_ra.register_id " +
				"		FROM olp_register_activity in_ra " +
				"		WHERE in_ra.activity_id in (:activityIdSet) ) " 	+
				"	AND ra.STATUS_CANCLE_ACTIVITY is null " +	
				"order by r.register_number asc, ap.customer_code asc,  pln.id asc, ex.id asc ,a.id asc, ra.id asc";
		
		String where2= "" +
				"where " +
				"	r.fiscal_year = :fiscalYear " +
				"	AND r.id not in (" +
				"		SELECT in_ra.register_id " +
				"		FROM olp_register_activity in_ra " +
				"		WHERE in_ra.activity_id in (:activityIdSet) ) "	+
				"	AND ra.STATUS_CANCLE_ACTIVITY is null " +
				"order by  r.register_number asc, ra.id asc, ap.customer_code asc,  pln.id asc, ex.id asc ,a.id asc";
		
//		String sql2 = "" +
//				"SELECT * " +
//				"FROM OLP_REGISTER r " +
//				"WHERE r.fiscal_year = ? and 221=?";
		
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		params.put("activityIdSet", activityIdSet);
		
		
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				allExceptActivities?sql1+where2:sql1+where1,
				params,
				genericRowMapper
				);
		
//		logger.debug(sql);
		logger.debug("fiscalYear: " + fiscalYear);
		logger.debug("activityIdList: " + activityIdSet);
		logger.debug("returnList.size() : " + returnList.size());
		
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findApplicantByCustomerCode(
			String customerCode, Integer fiscalYear) {
		
		logger.debug("findApplicantByCustomerCode: " + customerCode + " fiscalYear: "+ fiscalYear );
		
		String whereFiscalYear = "";
		if(fiscalYear != null) {
			whereFiscalYear = " and reg.fiscal_year = " + fiscalYear + " ";
		} else {
			whereFiscalYear = " and reg.fiscal_year is not null ";
		}
		
		String sql = "" +
				"select distinct(app.ID), app.CUSTOMER_CODE, app.CUSTOMER_NAME_CANDIDATE " +
				"from olp_applicant app, olp_register reg " +
				"where " +
				"	app.id = reg.APPLICANT_ID " + 
				"	and app.CUSTOMER_CODE like :customerCode " +
					whereFiscalYear +
				"order by app.CUSTOMER_CODE ASC";
		
		logger.debug(sql);
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("customerCode", "%" + customerCode + "%");
		
		List <Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql,
				params,
				genericRowMapper
				);
		
		
		return returnList;
	}

	
	@Override
	public List<Map<String, Object>> findRegistrationsByFiscalYearAndCustomerCode(
			String fiscalYear, String customerCode, Set<Integer> activityIdSet) {
		
		/**
		 * select r.register_number, ap.customer_code, ap.customer_name_candidate, r.id, ap.id, a.id
from olp_register r inner join olp_applicant ap on r.applicant_id = ap.id
  inner join olp_register_activity ra on ra.register_id = r.id 
  inner join olp_activity a on ra.activity_id = a.id
where a.activity_code like 'D01' and r.fiscal_year='2555'
;
		 */
		
		String sql1 = "" +
				"select r.register_number " +
				"	, r.end_payment_date " +
				"	, ap.customer_code " +
				"	, ap.customer_name_candidate " +
				"	, a.activity_code " +
				"	, a.start_activity_date " +
				" 	, a.round " +
				"	, nvl(ap.tel_no, \' \') tel_no " +
				"	, nvl(ap.fax_no, \' \') fax_no " +
				"	, ap.email " +
				"	, ex.example_name " +
				"	, pln.branch_name " +
				"	, a.activity_name " +
				"	, a.price " +
				"	, c.company_th_receipt " +
				"	, c.company_en_receipt " +
				"	, c.add_receipt " +
				"	, p.province_name " +
				"	, nvl(d.amphur_name, \' \') amphur_name " +
				"	, nvl(t.tambon_name, \' \') tambon_name " +
				"	, decode(nvl(c.tambon_id_receipt,\'0\'), \'0\', \' \', " +
                "  			decode(p.province_id, 21 , \'แขวง\'||t.tambon_name, " +
                "  					\'ตำบล\'||t.tambon_name )) tambon " +
                "	, decode(p.province_id, 21 , \'เขต\'||d.amphur_name, " +
				"			\'อำเภอ\'||d.amphur_name ) amphur " +
				"	, decode(p.province_id, 21 , p.province_name, "+
				"			\'จังหวัด\'||p.province_name ) province " +
				"	, c.postcode_receipt postcode " +
				"	, ra.exam_num "	+ 
				"	, ra.amount ra_amount" +
				"	, upper(substr(a.activity_code,1,2)) code2Letter " +
				"from " +
				"	olp_register r inner join olp_applicant ap on r.applicant_id = ap.id " +
				"	inner join olp_register_activity ra on ra.register_id = r.id " +  
				"	inner join olp_activity a on ra.activity_id = a.id " +
				" 	inner join olp_example ex on a.olp_example_id = ex.id " +
				"	inner join olp_plan pln on ex.olp_plan_id = pln.id " +
				"	inner join olp_company c on ap.olp_ref_company_id = c.id " +
				"	left outer join glb_province p on c.province_id_receipt = p.province_id " +
				"	left outer join glb_district d on c.district_id_receipt = d.amphur_id " +
				"	left outer join glb_tambon t on c.tambon_id_receipt = t.tambon_id " + 
				"where " +
				"	r.fiscal_year = :fiscalYear " +
				" 	AND ap.id = :customerCode  " +
				"	AND ra.activity_id in (:activityIdSet) " +
				"	AND ra.STATUS_CANCLE_ACTIVITY is null "	+
				"order by r.register_number asc, ra.id asc, ap.customer_code asc,  pln.id asc, a.activity_code asc";
		
		
		
//		String sql2 = "" +
//				"SELECT * " +
//				"FROM OLP_REGISTER r " +
//				"WHERE r.fiscal_year = ? and 221=?";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		params.put("customerCode", customerCode);
		params.put("activityIdSet", activityIdSet);
		
		logger.debug(">>>>SQL1 :\n"+ sql1);
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql1,
				params,
				genericRowMapper
				);
		
		logger.debug(sql1);
		logger.debug("fiscalYear: " + fiscalYear);
		logger.debug("customerCode: " + customerCode);
		logger.debug("activityIdSet: " + activityIdSet);
		logger.debug("returnList.size() : " + returnList.size());
		
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findRegisterNumberByLeadingString(String firstString, String lastString) {
		// TODO Auto-generated method stub
		
		String sql1 = "" +
				"SELECT ID, REGISTER_NUMBER " +
				"FROM OLP_REGISTER " +
				"WHERE REGISTER_NUMBER LIKE :searchRegisterNumber " +
				"ORDER BY REGISTER_NUMBER ASC ";
		
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		logger.debug(firstString+"%"+lastString);
		
		params.put("searchRegisterNumber", firstString+"%"+lastString);
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql1,
				params,
				genericRowMapper
				);
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findRegisterNumber(String fiscalYear,
			String firstRegisterNumber, String lastRegisterNumber, String activityType) {
		/**
		'ID', 'REGISTER_NUMBER', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE', 'ACTIVITIES'
		
		 * select reg.id, reg.register_number, act.activity_name, act.activity_code, act.id
from olp_register reg, 
  olp_register_activity reg_act,
  olp_activity act
where reg.id = reg_act.register_id 
  and reg_act.activity_id = act.id
  and reg.id=3081
order by act.activity_code;
		 */
		
		String activitySearch = " olp_get_act_from_reg_id(reg.id) ";
		String activityWhere = " a.activity_code like '%' ";
		
		if(activityType.equals("qc") ){
			activitySearch = " olp_get_act_qc_from_reg_id(reg.id) ";
			activityWhere = " upper(a.activity_code) like 'QC%' ";
		} else if(activityType.equals("act")) {
			activitySearch = " olp_get_act_notqc_from_reg_id(reg.id) ";
			activityWhere = " upper(a.activity_code) not like 'QC%' ";
		}
		
		String sql1 = "" 
				+ "SELECT reg.id, reg.register_number, ap.customer_name_candidate, " 
				+ " 	" + activitySearch + " activities, ap.customer_code " 
				+ "FROM OLP_REGISTER reg, olp_applicant ap" 
				+ "	" 
				+ "WHERE  reg.applicant_id = ap.id " 
				+ "	and reg.register_Number = :registerNumber "
				+ "	and reg.id in ( " 
				+ "		SELECT r.id "
				+ "		FROM OLP_REGISTER r, OLP_ACTIVITY a, OLP_REGISTER_ACTIVITY ra  "
				+ "		WHERE r.id = ra.register_id and a.id = ra.activity_id "
				+ "			AND " + activityWhere 
				+ "			AND r.register_Number = :registerNumber "	
				+ "	) " 
				+ "ORDER BY reg.id";
		String sql2 = ""
				+ "SELECT reg.id, reg.register_number, ap.customer_name_candidate, " 
				+ " 	" + activitySearch + " activities, ap.customer_code " 
				+ "FROM OLP_REGISTER reg, olp_applicant ap" 
				+ "	" 
				+ "WHERE  reg.applicant_id = ap.id " 
				+ "	and reg.fiscal_year = :fiscalYear "
				+ "	and reg.id in ( " 
				+ "		SELECT r.id "
				+ "		FROM OLP_REGISTER r, OLP_ACTIVITY a, OLP_REGISTER_ACTIVITY ra "
				+ "		WHERE r.id = ra.register_id and a.id = ra.activity_id "
				+ "			AND " + activityWhere 
				+ "			AND r.fiscal_year = :fiscalYear "	
				+ "	) " 
				+ "ORDER BY reg.id";
		String sql3 = " "
				+ "SELECT reg.id, reg.register_number, ap.customer_name_candidate, " 
				+ " 	" + activitySearch + " activities, ap.customer_code " 
				+ "FROM OLP_REGISTER reg, olp_applicant ap" 
				+ "	" 
				+ "WHERE  reg.applicant_id = ap.id " 
				+ "	and reg.id in ( " 
				+ "		SELECT r.id "
				+ "		FROM OLP_REGISTER r, OLP_ACTIVITY a, OLP_REGISTER_ACTIVITY ra "
				+ "		WHERE r.id = ra.register_id and a.id = ra.activity_id "
				+ "			AND " + activityWhere 
				+ "			AND r.register_Number >=  :firstRegisterNumber "
				+ "			AND r.register_Number <= :lastRegisterNumber " 
				+ "	) " 
				+ "ORDER BY reg.id";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		String sql;
		if(emptyString(firstRegisterNumber) && emptyString(lastRegisterNumber)) {
			if(!emptyString(fiscalYear)) {
				params.put("fiscalYear", fiscalYear);
				sql=sql2;
			} else {
				return null;
			}
		} else {
			if(!emptyString(firstRegisterNumber)) {
				if(!emptyString(lastRegisterNumber)) {
					//
					sql=sql3;
					params.put("firstRegisterNumber", firstRegisterNumber);
					params.put("lastRegisterNumber", lastRegisterNumber);
					
				} else {
					sql=sql1;
					params.put("registerNumber", firstRegisterNumber);
				}
				
			} else {
				return null;
			}
			
		}
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql,
				params,
				genericRowMapper
				);
		
		return returnList;
	}

	@Override
	public List<Map<String, Object>> findRegistrationsById(
			Set<Integer> registerIds, String activitySearch) {
		String sql = "" +
				"select r.register_number " +
				"	, r.end_payment_date " +
				"	, ap.customer_code " +
				"	, ap.customer_name_candidate " +
				"	, a.activity_code " +
				" 	, a.round " +
				"	, upper(substr(a.activity_code,1,2)) code2Letter" +
				"	, a.start_activity_date " +
				"	, nvl(ap.tel_no, \' \') tel_no " +
				"	, nvl(ap.fax_no, \' \') fax_no " +
				"	, ap.email " +
				"	, ex.example_name " +
				"	, pln.branch_name " +
				"	, a.activity_name " +
				"	, a.price " +
				"	, c.company_th_receipt " +
				"	, c.company_en_receipt " +
				"	, c.add_receipt " +
				"	, p.province_name " +
				"	, nvl(d.amphur_name, \' \') amphur_name " +
				"	, nvl(t.tambon_name, \' \') tambon_name " +
				"	, decode(nvl(c.tambon_id_receipt,\'0\'), \'0\', \' \', " +
                "  			decode(p.province_id, 21 , \'แขวง\'||t.tambon_name, " +
                "  					\'ตำบล\'||t.tambon_name )) tambon " +
                "	, decode(p.province_id, 21 , \'เขต\'||d.amphur_name, " +
				"			\'อำเภอ\'||d.amphur_name ) amphur " +
				"	, decode(p.province_id, 21 , p.province_name, "+
				"			\'จังหวัด\'||p.province_name ) province " +
				"	, c.postcode_receipt postcode " +
				"	, ra.exam_num exam_num " +
				"	, ra.amount ra_amount " + 
				"from " +
				"	olp_register r inner join olp_applicant ap on r.applicant_id = ap.id " +
				"	inner join olp_register_activity ra on ra.register_id = r.id " +  
				"	inner join olp_activity a on ra.activity_id = a.id " +
				" 	inner join olp_example ex on a.olp_example_id = ex.id " +
				"	inner join olp_plan pln on ex.olp_plan_id = pln.id " +
				"	inner join olp_company c on ap.olp_ref_company_id = c.id " +
				"	left outer join glb_province p on c.province_id_receipt = p.province_id " +
				"	left outer join glb_district d on c.district_id_receipt = d.amphur_id " +
				"	left outer join glb_tambon t on c.tambon_id_receipt = t.tambon_id " + 
				"where " +
				"	 r.id in (:registerIds) and ra.STATUS_CANCLE_ACTIVITY is null "
				+ "	AND upper(a.activity_code)  "	+ activitySearch +
				"order by ap.customer_code asc, ra.id asc, r.register_number asc, pln.id asc, a.activity_code asc";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("registerIds", registerIds);
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql,
				params,
				genericRowMapper
				);
		
		return returnList;
	}
	
	private Boolean emptyString(String s ) {
		return s == null || s.length() == 0;
	}

	@Override
	public List<Map<String, Object>> findRegistrationsForEmsByIds(
			Set<Integer> registerIds) {
		// TODO Auto-generated method stub
		String sql = " " +
				"SELECT reg.id, reg.register_number, ap.customer_name_candidate, " +
				" 	olp_get_act_from_reg_id(reg.id) activities, ap.customer_code, " +
				"	c.company_th_applicant, p.province_name " +
				"FROM OLP_REGISTER reg, olp_applicant ap, " +
				"  olp_company c,	" +
				"  glb_province p " + 
				"WHERE  reg.applicant_id = ap.id " +
				"	and ap.olp_ref_company_id = c.id " +
				"   and p.province_id = c.province_id_applicant	" +
				"	and reg.id in (:registerIds) " +
				"ORDER BY reg.id ";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("registerIds", registerIds);
		
		List<Map<String, Object>> returnList = this.jdbcTemplate.query(
				sql,
				params,
				genericRowMapper
				);
		
		return returnList;

	}



}
