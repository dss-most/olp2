package th.go.dss.olp.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class authController {

	@RequestMapping("/login") 
	public String login(Model model) {
		
		return "auth/login";
	}
}
