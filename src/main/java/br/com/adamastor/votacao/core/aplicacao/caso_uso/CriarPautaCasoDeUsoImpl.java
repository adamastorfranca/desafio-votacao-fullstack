package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosCriacaoPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.CriarPautaCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CriarPautaCasoDeUsoImpl implements CriarPautaCasoDeUso {

    private final PortaRepositorioPauta portaRepositorioPauta;

    @Override
    public Pauta executar(DadosCriacaoPautaDTO dadosCriacaoPautaDTO) {
        var novoId = UUID.randomUUID();

        log.info("Iniciando criação de nova pauta. ID gerado: {}, Título: '{}'", novoId, dadosCriacaoPautaDTO.titulo());

        var pauta = Pauta.builder()
                .titulo(dadosCriacaoPautaDTO.titulo())
                .descricao(dadosCriacaoPautaDTO.descricao())
                .dataHoraCriacao(Instant.now())
                .build();

        var pautaSalva = portaRepositorioPauta.salvar(pauta);

        log.info("Pauta criada com sucesso. ID: {}", pautaSalva.getId());

        return pautaSalva;
    }

}