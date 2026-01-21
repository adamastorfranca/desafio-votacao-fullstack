package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.core.aplicacao.porta.entrada.AbrirSessaoCasoDeUso;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.AbrirSessaoRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.SessaoRespostaDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper.SessaoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping(ApiConstantes.ROTA_SESSOES_V1)
@RequiredArgsConstructor
@Transactional
@Tag(name = "Sessões", description = "Gerenciamento de abertura de sessões de votação")
public class SessaoController {

    private final AbrirSessaoCasoDeUso abrirSessaoCasoDeUso;
    private final SessaoWebMapper mapper;

    @PostMapping
    @Operation(summary = "Abrir sessão de votação", description = "Abre uma sessão para uma pauta. Se o tempo não for informado, o padrão é 1 minuto.")
    public ResponseEntity<SessaoRespostaDTO> abrir(@RequestBody @Valid AbrirSessaoRequisicaoDTO requisicao, UriComponentsBuilder uriBuilder) {
        log.info("Recebendo requisição REST para abrir sessão na pauta: {}", requisicao.pautaId());

        var dadosAbertura = mapper.paraDtoAplicacao(requisicao);
        var sessaoAberta = abrirSessaoCasoDeUso.executar(dadosAbertura);
        var resposta = mapper.paraResposta(sessaoAberta);

        var uri = uriBuilder
                .path(ApiConstantes.ROTA_SESSOES_V1 + "/{id}")
                .buildAndExpand(sessaoAberta.getId())
                .toUri();

        log.info("Sessão aberta com sucesso via REST. ID: {}", sessaoAberta.getId());

        return ResponseEntity.created(uri).body(resposta);
    }
}