package br.com.adamastor.votacao.infraestrutura.configuracao.bean;

import br.com.adamastor.votacao.core.aplicacao.caso_uso.CriarPautaCasoDeUsoImpl;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.CriarPautaCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoPauta {

    @Bean
    public CriarPautaCasoDeUso criarPautaCasoDeUso(PortaRepositorioPauta portaRepositorioPauta) {
        return new CriarPautaCasoDeUsoImpl(portaRepositorioPauta);
    }

}