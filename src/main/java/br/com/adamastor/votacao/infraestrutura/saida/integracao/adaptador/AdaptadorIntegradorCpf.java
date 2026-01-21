package br.com.adamastor.votacao.infraestrutura.saida.integracao.adaptador;

import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaIntegradorCpf;
import br.com.adamastor.votacao.infraestrutura.saida.integracao.cliente.CpfClienteApi;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorIntegradorCpf implements PortaIntegradorCpf {

    private final CpfClienteApi cpfClienteApi;

    @Override
    @Cacheable(value = "cpf-validation", key = "#cpf", unless = "#result == false")
    @CircuitBreaker(name = "integracao-cpf", fallbackMethod = "podeVotarFallback")
    @Retry(name = "integracao-cpf")
    public boolean podeVotar(String cpf) {
        log.info("Iniciando validação moderna de CPF: {}", cpf);

        var resposta = cpfClienteApi.buscarStatusCpf(cpf);

        boolean apto = "ABLE_TO_VOTE".equals(resposta.getStatus());
        log.debug("Resultado da validação para o CPF {}: {}", cpf, apto);

        return apto;
    }

    public boolean podeVotarFallback(String cpf, Exception ex) {
        log.error("ERRO INTEGRACAO: Falha ao validar CPF {} após tentativas. Motivo: {}. Aplicando Fallback.",
                cpf, ex.getMessage());
        return true;
    }
}