package th.go.dss.olp.auth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class BackOfficeSecDaoJdbc implements BackOfficeSecDao {

	private static final Logger logger = LoggerFactory.getLogger(BackOfficeSecDaoJdbc.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource ds) {
		this.jdbcTemplate = new JdbcTemplate(ds);
	}
	
	private RowMapper<GrantedAuthority> authorityRowMapper = new RowMapper<GrantedAuthority>() {
		@SuppressWarnings("serial")
		public GrantedAuthority mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			
			return new GrantedAuthority() {
				
				@Override
				public String getAuthority() {
					try {
						return "ROLE_" + rs.getString(1);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			
		
		}
	};

	
	@Override
	public List<GrantedAuthority> getGrantedAuthority(Authentication auth) {
		String sql = "" +
				"select g.group_code from s_user s, s_group_list gl, s_group g " +
				"where  s.id = gl.s_user_id and g.id = gl.s_group_id  " +
				"		and g.GROUP_CODE like 'OLP_ADMIN' " +
				"	s.login like UPPER(?) and s.password like ? ";
	
		List<GrantedAuthority> returnList = this.jdbcTemplate.query(
				sql,
				authorityRowMapper,
				auth.getName(),
				auth.getCredentials()
				);

		if(logger.isDebugEnabled()) {
			logger.debug("user attempt login : " + auth.getName());
			for(GrantedAuthority grantedAuth : returnList) {
				logger.debug("Granted : " + grantedAuth.getAuthority());
			}
		}
		
		return returnList;
	}

}
