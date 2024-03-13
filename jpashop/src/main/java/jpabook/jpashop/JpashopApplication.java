package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(JpashopApplication.class);
		app.setRegisterShutdownHook(false); // JVM 종료 훅 등록 비활성화
		app.run(args);
//		SpringApplication.run(JpashopApplication.class, args);
	}
}
