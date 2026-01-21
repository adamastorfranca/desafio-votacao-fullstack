package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosVotoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.RegistrarVotoCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaIntegradorCpf;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import br.com.adamastor.votacao.core.dominio.excecao.EntidadeNaoEncontradaException;
import br.com.adamastor.votacao.core.dominio.excecao.RegraNegocioException;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class RegistrarVotoCasoDeUsoImpl implements RegistrarVotoCasoDeUso {

    private final PortaRepositorioVoto portaRepositorioVoto;
    private final PortaRepositorioSessao portaRepositorioSessao;
    private final PortaIntegradorCpf portaIntegradorCpf;

    @Override
    public Voto executar(DadosVotoDTO dados) {
        var novoId = UUID.randomUUID();

        log.info("Iniciando registro de voto. ID Gerado: {}, Sessão ID: {}, CPF: {}", novoId, dados.sessaoId(), dados.cpfAssociado());

        var sessao = portaRepositorioSessao.obterPorId(dados.sessaoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Sessão não encontrada com ID: " + dados.sessaoId()));

        if (!sessao.isAberta()) {
            throw new RegraNegocioException("Sessão não está aberta ou já foi encerrada com ID: " + dados.sessaoId());
        }

        if (portaRepositorioVoto.existeVotoDoAssociadoNaSessao(dados.sessaoId(), dados.cpfAssociado())) {
            throw new RegraNegocioException("Associado já votou nesta sessão: " + dados.cpfAssociado());
        }

        if (!portaIntegradorCpf.podeVotar(dados.cpfAssociado())) {
            throw new RegraNegocioException("Associado não está autorizado a votar: " + dados.cpfAssociado());
        }

        var voto = Voto.builder()
                .id(novoId)
                .sessaoId(dados.sessaoId())
                .cpfAssociado(dados.cpfAssociado())
                .opcao(dados.opcao())
                .dataHoraCriacao(Instant.now())
                .build();

        var votoSalvo = portaRepositorioVoto.salvar(voto);

        log.info("Voto registrado com sucesso. ID: {}, Opção: {}", votoSalvo.getId(), votoSalvo.getOpcao());

        return votoSalvo;
    }
}

