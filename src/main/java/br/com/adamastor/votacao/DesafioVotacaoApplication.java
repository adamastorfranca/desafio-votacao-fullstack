package br.com.adamastor.votacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DesafioVotacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioVotacaoApplication.class, args);
	}

}
