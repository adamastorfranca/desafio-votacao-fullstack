package br.com.adamastor.votacao.infraestrutura.configuracao.bean;

import br.com.adamastor.votacao.core.aplicacao.caso_uso.AbrirSessaoCasoDeUsoImpl;
import br.com.adamastor.votacao.core.aplicacao.caso_uso.CriarPautaCasoDeUsoImpl;
import br.com.adamastor.votacao.core.aplicacao.caso_uso.RegistrarVotoCasoDeUsoImpl;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.AbrirSessaoCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.CriarPautaCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.RegistrarVotoCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaIntegradorCpf;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoVotacao {

    @Bean
    public CriarPautaCasoDeUso criarPautaCasoDeUso(PortaRepositorioPauta portaRepositorioPauta) {
        return new CriarPautaCasoDeUsoImpl(portaRepositorioPauta);
    }

    @Bean
    public AbrirSessaoCasoDeUso abrirSessaoCasoDeUso(
            PortaRepositorioSessao portaRepositorioSessao,
            PortaRepositorioPauta portaRepositorioPauta) {
        return new AbrirSessaoCasoDeUsoImpl(portaRepositorioSessao, portaRepositorioPauta);
    }

    @Bean
    public RegistrarVotoCasoDeUso registrarVotoCasoDeUso(
            PortaRepositorioVoto portaRepositorioVoto,
            PortaRepositorioSessao portaRepositorioSessao,
            PortaIntegradorCpf portaIntegradorCpf) {
        return new RegistrarVotoCasoDeUsoImpl(portaRepositorioVoto, portaRepositorioSessao, portaIntegradorCpf);
    }

}