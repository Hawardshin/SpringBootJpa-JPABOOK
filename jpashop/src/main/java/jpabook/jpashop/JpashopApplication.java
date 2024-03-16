package jpabook.jpashop;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	//이건 이런 방법이 있다고 설명은 하는거지
	//실제로 외부에 엔티티를 노출할 일은 없을 것이니까 너무 걱정말고 일단 듣자.
	// 이건 지연로딩인 경우 null로 나오도록 해줍니다.
	//이렇게 하면 성능상에서도 엔티티 스펙이 바뀌면 api 다 바뀌니까 안 좋습니다.
	@Bean
	Hibernate5JakartaModule hibernate5Module() {
		Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
//		hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5JakartaModule;
	}
}
