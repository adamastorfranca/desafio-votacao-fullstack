package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.ResultadoVotacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ProcessarSessoesEncerradasCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorResultado;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoResultado;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ProcessarSessoesEncerradasCasoDeUsoImpl implements ProcessarSessoesEncerradasCasoDeUso {

    private final PortaRepositorioSessao portaRepositorioSessao;
    private final PortaRepositorioVoto portaRepositorioVoto;
    private final PortaPublicadorResultado portaPublicadorResultado;

    @Override
    @Transactional
    public void executar() {
        var sessoesParaProcessar = portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas();

        if (sessoesParaProcessar.isEmpty()) {
            return;
        }

        log.info("Iniciando processamento de {} sessões encerradas.", sessoesParaProcessar.size());
        sessoesParaProcessar.forEach(this::processarSessaoIndividual);
    }

    private void processarSessaoIndividual(Sessao sessao) {
        try {
            log.info("Processando resultados da sessão ID: {}", sessao.getId());

            var contagem = portaRepositorioVoto.contarVotosPorOpcao(sessao.getId());
            var resultadoCalculado = calcularEstatisticas(contagem);

            sessao.registrarResultado(
                    resultadoCalculado.totalVotos(),
                    resultadoCalculado.totalSim(),
                    resultadoCalculado.totalNao(),
                    resultadoCalculado.resultado(),
                    SessaoStatus.ENCERRADA
            );

            portaRepositorioSessao.salvar(sessao);
            portaPublicadorResultado.publicar(sessao.getId(), resultadoCalculado);

            log.info("Sessão {} processada. Resultado: {}", sessao.getId(), resultadoCalculado.resultado());
        } catch (Exception e) {
            log.error("Erro ao processar sessão ID: {}", sessao.getId(), e);
        }
    }

    private ResultadoVotacaoDTO calcularEstatisticas(List<ContagemVotosDTO> votos) {
        Map<VotoOpcao, Long> mapaVotos = votos.stream()
                .collect(Collectors.toMap(ContagemVotosDTO::opcao, ContagemVotosDTO::quantidade));

        long votosSim = mapaVotos.getOrDefault(VotoOpcao.SIM, 0L);
        long votosNao = mapaVotos.getOrDefault(VotoOpcao.NAO, 0L);
        long totalVotos = votosSim + votosNao;

        String decisao = obterDecisao(votosSim, votosNao, totalVotos);

        return new ResultadoVotacaoDTO(totalVotos, votosSim, votosNao, decisao);
    }

    private String obterDecisao(long votosSim, long votosNao, long totalVotos) {
        if (totalVotos == 0) {
            return SessaoResultado.SEM_VOTOS.name();
        }
        if (votosSim > votosNao) {
            return SessaoResultado.APROVADA.name();
        }
        if (votosNao > votosSim) {
            return SessaoResultado.REPROVADA.name();
        }
        return SessaoResultado.EMPATE.name();
    }
}
