package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosAberturaSessaoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.AbrirSessaoCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.dominio.excecao.EntidadeNaoEncontradaException;
import br.com.adamastor.votacao.core.dominio.excecao.RegraNegocioException;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class AbrirSessaoCasoDeUsoImpl implements AbrirSessaoCasoDeUso {

    private final PortaRepositorioSessao portaRepositorioSessao;
    private final PortaRepositorioPauta portaRepositorioPauta;

    private static final long TEMPO_DEFAULT_MINUTOS = 1L;

    @Override
    public Sessao executar(DadosAberturaSessaoDTO dados) {
        log.info("Iniciando abertura de sessão para pauta: {}", dados.pautaId());

        if (!portaRepositorioPauta.existePorId(dados.pautaId())) {
            throw new EntidadeNaoEncontradaException("Pauta não encontrada com ID: " + dados.pautaId());
        }

        if (portaRepositorioSessao.existeSessaoAbertaParaPauta(dados.pautaId())) {
            throw new RegraNegocioException("Já existe uma sessão aberta para esta pauta.");
        }

        long minutos = dados.tempoEmMinutos() != null && dados.tempoEmMinutos() > 0
                ? dados.tempoEmMinutos()
                : TEMPO_DEFAULT_MINUTOS;

        var inicio = Instant.now();
        var termino = inicio.plus(Duration.ofMinutes(minutos));

        var sessao = Sessao.builder()
                .pautaId(dados.pautaId())
                .dataHoraInicio(inicio)
                .dataHoraTermino(termino)
                .status(SessaoStatus.ABERTA)
                .build();

        var sessaoSalva = portaRepositorioSessao.salvar(sessao);

        log.info("Sessão aberta com sucesso. ID: {}, Término previsto: {}", sessaoSalva.getId(), termino);

        return sessaoSalva;
    }
}