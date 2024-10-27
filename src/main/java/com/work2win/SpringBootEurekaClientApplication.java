package com.work2win;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.netflix.discovery.EurekaClient;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class SpringBootEurekaClientApplication implements WelcomeRepo{
	
	@Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEurekaClientApplication.class, args);
	}

	@Override
	public String welcome() {
		// 
		return String.format("Welcome from '%s'!!", eurekaClient.getApplication(appName).getName());
	}
	@RestController
	class ServiceInstanceRestController {

	    private WhoAmI whoAmI;

	    private DiscoveryClient discoveryClient;
	    private final RestClient restClient;

	    @Autowired
	    public ServiceInstanceRestController(WhoAmI whoAmI, DiscoveryClient discoveryClient, RestClient.Builder restClientBuilder) {
	        this.whoAmI = whoAmI;
	        this.discoveryClient = discoveryClient;
	        restClient = restClientBuilder.build();
	    }
	
	   @RequestMapping("/")
	    public String index() {
	        return
	                "<ul>" +
	                   "<li><a href=\"/whoami\">whoami</a>" +
	                   "<li><a href=\"/instances\">instances</a>" +
	                   "<li><a href=\"/transactions\">transactions</a>" +
	                "</ul>";
	    }

	    @RequestMapping("/instances")
	    public List<ServiceInstance> clients() {
	        return this.discoveryClient.getInstances(whoAmI.springApplicationName);
	    }

	    @RequestMapping("/whoami")
	    public WhoAmI whoami() {
	        return whoAmI;
	    }
	    
	    @GetMapping("/helloWorld")
		public String helloWorld() {
			return "Hello world from Service SpringBoot-EurekaClient!";
		}
	    
	    @GetMapping("/transactions")
		public String transactions() {
	    	
	    	ServiceInstance serviceInstance = discoveryClient.getInstances("SpringBoot-App-EurekaClient").get(0);
	    	String serviceAResponse = restClient.get()
					.uri(serviceInstance.getUri() + "/transaction/id")
					.retrieve()
					.body(String.class);
			return serviceAResponse;
		}
	}

	@Component
	class WhoAmI {
	    @Value("${spring.application.name}")
	    public String springApplicationName;

	    @Value("${server.port:8080}")
	    public String serverPort;
	}
	

	
	

}
