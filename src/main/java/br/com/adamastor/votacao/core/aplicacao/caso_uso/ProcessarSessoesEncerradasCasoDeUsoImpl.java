package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.ResultadoVotacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ProcessarSessoesEncerradasCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorResultado;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoResultado;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            var resultadoCalculado = calcularResultado(contagem);

            sessao.registrarResultado(
                    resultadoCalculado.totalVotos(),
                    resultadoCalculado.totalSim(),
                    resultadoCalculado.totalNao(),
                    resultadoCalculado.resultado()
            );

            portaRepositorioSessao.salvar(sessao);
            portaPublicadorResultado.publicar(sessao.getId(), resultadoCalculado);

            log.info("Sessão {} processada. Resultado: {}", sessao.getId(), resultadoCalculado.resultado());
        } catch (Exception e) {
            log.error("Erro ao processar sessão ID: {}", sessao.getId(), e);
        }
    }

    private ResultadoVotacaoDTO calcularResultado(List<ContagemVotosDTO> contagem) {
        long sim = 0;
        long nao = 0;

        for (ContagemVotosDTO c : contagem) {
            if (VotoOpcao.SIM.equals(c.opcao())) {
                sim = c.quantidade();
            } else if (VotoOpcao.NAO.equals(c.opcao())) {
                nao = c.quantidade();
            }
        }

        long total = sim + nao;
        String decisao;

        if (sim > nao) {
            decisao = SessaoResultado.APROVADA.getDescricao();
        } else if (nao > sim) {
            decisao = SessaoResultado.REPROVADA.getDescricao();
        } else {
            decisao = total == 0 ? SessaoResultado.SEM_VOTOS.getDescricao() : SessaoResultado.EMPATE.getDescricao();
        }

        return new ResultadoVotacaoDTO(total, sim, nao, decisao);
    }
}