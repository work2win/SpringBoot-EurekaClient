package com.work2win;

import org.springframework.web.bind.annotation.RequestMapping;

public interface WelcomeRepo {
	
	@RequestMapping("/home")
    String welcome();

}
