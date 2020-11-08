package th.go.dss.olp.auth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class BackOfficeSecDaoJdbc implements BackOfficeSecDao {

	private static final Logger logger = LoggerFactory.getLogger(BackOfficeSecDaoJdbc.class);

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
//	@Autowired
//	public void setDataSource(DataSource ds) {
//		this.jdbcTemplate = new JdbcTemplate(ds);
//	}
	
	private RowMapper<GrantedAuthority> authorityRowMapper = new RowMapper<GrantedAuthority>() {
		@SuppressWarnings("serial")
		public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			String role = rs.getString(1);
			logger.debug("Granted: ROLE_" + role);
			
			return new GrantedAuthority() {
				@Override
				public String getAuthority() {
						return "ROLE_" + role;
				}
			};
			
		
		}
	};

	
	@Override
	public List<GrantedAuthority> getGrantedAuthority(Authentication auth) {
		String sql = "" +
				"select g.group_code from s_user s, s_group_list gl, s_group g " +
				"where  s.id = gl.s_user_id and g.id = gl.s_group_id and " +
				"	s.login like UPPER(:name) and s.password like :password ";
	
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", auth.getName());
		params.put("password", auth.getCredentials());
		
		
//		List<GrantedAuthority> returnList = this.jdbcTemplate.query(
//				sql,params,
//				authorityRowMapper
//				);
		
		this.jdbcTemplate.query(sql, params, authorityRowMapper);
		
		List<GrantedAuthority> returnList = this.jdbcTemplate.query(
				sql,params, authorityRowMapper);

		if(logger.isDebugEnabled()) {
			logger.debug("user attempt login : " + auth.getName());
			for(GrantedAuthority grantedAuth : returnList) {
				logger.debug("Granted : " + grantedAuth.getAuthority());
			}
		}
		
		return returnList;
	}

}
