package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.core.aplicacao.porta.entrada.CriarPautaCasoDeUso;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.CriarPautaRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.PautaRespostaDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper.PautaWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping(ApiConstantes.ROTA_PAUTAS)
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Gerenciamento de pautas para votação")
public class PautaController {

    private final CriarPautaCasoDeUso criarPautaCasoDeUso;
    private final PautaWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar nova pauta", description = "Cria uma nova pauta para futura sessão de votação")
    public ResponseEntity<PautaRespostaDTO> criar(@RequestBody @Valid CriarPautaRequisicaoDTO requisicao, UriComponentsBuilder uriBuilder) {
        log.info("Recebendo requisição REST para criar pauta: {}", requisicao.titulo());

        var dadosCriacao = mapper.paraDtoAplicacao(requisicao);
        var pautaCriada = criarPautaCasoDeUso.executar(dadosCriacao);
        var resposta = mapper.paraResposta(pautaCriada);

        var uri = uriBuilder
                .path(ApiConstantes.ROTA_PAUTAS + "/{id}")
                .buildAndExpand(pautaCriada.getId())
                .toUri();

        log.info("Pauta criada com sucesso via REST. ID: {}", pautaCriada.getId());

        return ResponseEntity.created(uri).body(resposta);
    }
}