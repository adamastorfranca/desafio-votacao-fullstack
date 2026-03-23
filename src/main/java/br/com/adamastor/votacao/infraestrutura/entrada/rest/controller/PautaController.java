package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.core.aplicacao.dto.*;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.CriarPautaCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ListarPautasDetalhadasCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ObterEstatisticasDashboardCasoDeUso;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.CriarPautaRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.PautaRespostaDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper.PautaWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping(ApiConstantes.ROTA_PAUTAS_V1)
@RequiredArgsConstructor
@Transactional
@Tag(name = "Pautas", description = "Gerenciamento de pautas para votação")
public class PautaController {

    private final CriarPautaCasoDeUso criarPautaCasoDeUso;
    private final ListarPautasDetalhadasCasoDeUso listarPautasDetalhadasCasoDeUso;
    private final ObterEstatisticasDashboardCasoDeUso obterEstatisticasDashboardCasoDeUso;
    private final PautaWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar nova pauta", description = "Cria uma nova pauta para futura sessão de votação")
    public ResponseEntity<PautaRespostaDTO> criar(@RequestBody @Valid CriarPautaRequisicaoDTO requisicao, UriComponentsBuilder uriBuilder) {
        log.info("Recebendo requisição REST para criar pauta: {}", requisicao.titulo());

        var dadosCriacao = mapper.paraDtoAplicacao(requisicao);
        var pautaCriada = criarPautaCasoDeUso.executar(dadosCriacao);
        var resposta = mapper.paraResposta(pautaCriada);

        var uri = uriBuilder
                .path(ApiConstantes.ROTA_PAUTAS_V1 + "/{id}")
                .buildAndExpand(pautaCriada.getId())
                .toUri();


        log.info("Pauta criada com sucesso via REST. ID: {}", pautaCriada.getId());

        return ResponseEntity.created(uri).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Listar pautas detalhadas", description = "Retorna pautas com filtros opcionais e paginação")
    public ResponseEntity<Page<PautaDetalhadaRespostaDTO>> listarDetalhadas(
            @Parameter(description = "Status da sessão (AGUARDANDO, ABERTA, ENCERRADA)")
            @RequestParam(required = false) String statusSessao,
            @Parameter(description = "Resultado da votação (SIM, NAO, EMPATE, SEM_VOTOS)")
            @RequestParam(required = false) String resultado,
            @Parameter(description = "Período para filtrar pautas criadas (HOJE, ULTIMO_7_DIAS, ULTIMOS_15_DIAS, ULTIMO_MES, ULTIMO_3_MESES, ULTIMO_6_MESES, ULTIMO_ANO, TODO_PERIODO)")
            @RequestParam(required = false) PeriodoFiltroEnum periodo,
            Pageable pageable) {
        log.info("Recebendo requisição REST para listar pautas detalhadas com filtros: statusSessao={}, resultado={}, periodo={}, paginação={}",
                 statusSessao, resultado, periodo, pageable);

        var filtro = new FiltroPautaDTO(statusSessao, resultado, periodo);
        var paginacao = new PaginacaoDTO(pageable.getPageNumber(), pageable.getPageSize());

        var pagina = listarPautasDetalhadasCasoDeUso.executar(filtro, paginacao);
        var pageImpl = new PageImpl<>(pagina.conteudo(), pageable, pagina.totalElementos());

        return ResponseEntity.ok(pageImpl);
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas do dashboard", description = "Retorna estatísticas das pautas com filtros opcionais")
    public ResponseEntity<EstatisticasDashboardDTO> obterEstatisticas(
            @Parameter(description = "Status da sessão (AGUARDANDO, ABERTA, ENCERRADA)")
            @RequestParam(required = false) String statusSessao,
            @Parameter(description = "Resultado da votação (APROVADA, REPROVADA, EMPATE, SEM_VOTOS)")
            @RequestParam(required = false) String resultado,
            @Parameter(description = "Período para filtrar pautas criadas (HOJE, ULTIMO_7_DIAS, ULTIMOS_15_DIAS, ULTIMO_MES, ULTIMO_3_MESES, ULTIMO_6_MESES, ULTIMO_ANO, TODO_PERIODO)")
            @RequestParam(required = false) PeriodoFiltroEnum periodo) {
        log.info("Recebendo requisição REST para obter estatísticas com filtros: statusSessao={}, resultado={}, periodo={}",
                 statusSessao, resultado, periodo);

        var filtro = new FiltroPautaDTO(statusSessao, resultado, periodo);
        var estatisticas = obterEstatisticasDashboardCasoDeUso.executar(filtro);

        return ResponseEntity.ok(estatisticas);
    }

}
