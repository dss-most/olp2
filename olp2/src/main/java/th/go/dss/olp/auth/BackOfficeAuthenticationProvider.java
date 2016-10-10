package th.go.dss.olp.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import th.go.dss.olp.auth.dao.BackOfficeSecDao;

@Service
public class BackOfficeAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private BackOfficeSecDao boSecDao;
	
	public BackOfficeSecDao getBoSecDao() {
		return boSecDao;
	}

	public void setBoSecDao(BackOfficeSecDao boSecDao) {
		this.boSecDao = boSecDao;
	}

	@Override
	public Authentication authenticate(Authentication auth)
			throws AuthenticationException {
		List<GrantedAuthority> AUTHORITIES = boSecDao.getGrantedAuthority(auth);
		
		if (AUTHORITIES != null && AUTHORITIES.size() > 0) {
			return new UsernamePasswordAuthenticationToken(auth.getName(),
					auth.getCredentials(), AUTHORITIES);
		}
		throw new BadCredentialsException("Bad Credentials");
	}

	@Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
	


}
