package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.core.aplicacao.porta.entrada.RegistrarVotoCasoDeUso;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.RegistrarVotoRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.VotoRespostaDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper.VotoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(ApiConstantes.RECURSO_VOTOS_V1)
@RequiredArgsConstructor
@Transactional
@Tag(name = "Votos", description = "Gerenciamento de votação em sessões")
public class VotoController {

    private final RegistrarVotoCasoDeUso registrarVotoCasoDeUso;
    private final VotoWebMapper mapper;

    @PostMapping
    @Operation(summary = "Registrar voto", description = "Registra um voto de um associado em uma sessão de votação aberta")
    public ResponseEntity<VotoRespostaDTO> registrar(
            @PathVariable UUID sessaoId,
            @RequestBody @Valid RegistrarVotoRequisicaoDTO requisicao,
            UriComponentsBuilder uriBuilder) {
        log.info("Recebendo requisição REST para registrar voto na sessão: {}, CPF: {}", sessaoId, requisicao.cpfAssociado());

        var dadosVoto = mapper.paraDtoAplicacao(requisicao, sessaoId);
        var votoRegistrado = registrarVotoCasoDeUso.executar(dadosVoto);
        var resposta = mapper.paraResposta(votoRegistrado);

        log.info("Voto registrado com sucesso via REST. ID do Voto: {}, Sessão: {}", votoRegistrado.getId(), sessaoId);

        var uri = uriBuilder
                .path(ApiConstantes.RECURSO_VOTOS_V1 + "/{votoId}")
                .buildAndExpand(sessaoId, votoRegistrado.getId())
                .toUri();

        return ResponseEntity.created(uri).body(resposta);
    }
}

