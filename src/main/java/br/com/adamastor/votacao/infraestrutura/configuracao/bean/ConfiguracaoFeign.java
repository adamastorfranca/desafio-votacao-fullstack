package br.com.adamastor.votacao.infraestrutura.configuracao.bean;

import br.com.adamastor.votacao.infraestrutura.saida.integracao.cliente.CpfClienteApiFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoFeign {

    @Bean
    public CpfClienteApiFallback clienteCpfFallback() {
        return new CpfClienteApiFallback();
    }

}

