package br.com.adamastor.votacao.infraestrutura.entrada.agendador;

import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ProcessarSessoesEncerradasCasoDeUso;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgendadorFechamentoSessao {

    private final ProcessarSessoesEncerradasCasoDeUso casoDeUso;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void verificarSessoesEncerradas() {
        log.info("Scheduler: Verificando sessões encerradas pendentes de processamento...");
        casoDeUso.executar();
    }

}
