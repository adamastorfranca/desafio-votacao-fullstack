package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.aplicacao.dto.EstatisticasDashboardDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.FiltroPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PautaDetalhadaRespostaDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoResultado;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.PautaPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioPautaJpa;
import br.com.adamastor.votacao.core.aplicacao.dto.PeriodoFiltroEnum;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorPautaPostgres implements PortaRepositorioPauta {

    private final RepositorioPautaJpa repositorioPautaJpa;
    private final PautaPersistenciaMapper mapper;

    @Override
    public Pauta salvar(Pauta pauta) {
        log.debug("Adaptador: Iniciando persistência da pauta: {}", pauta.getTitulo());

        var entidade = mapper.paraEntidade(pauta);
        if (entidade.getId() == null) {
            entidade.setId(UUID.randomUUID());
        }

        var entidadeSalva = repositorioPautaJpa.save(entidade);

        log.debug("Adaptador: Pauta persistida com ID: {}", entidadeSalva.getId());

        return mapper.paraDominio(entidadeSalva);
    }

    @Override
    public boolean existePorId(UUID id) {
        return repositorioPautaJpa.existsById(id);
    }

    @Override
    public PaginaDTO<PautaDetalhadaRespostaDTO> buscarComFiltros(FiltroPautaDTO filtro, PaginacaoDTO paginacao) {
        log.debug("Adaptador: Buscando pautas com filtros: {}", filtro);

        Specification<br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade> spec = construirEspecificacaoBase(filtro);

        Pageable pageable = PageRequest.of(paginacao.pagina(), paginacao.tamanho(), Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
        Page<br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade> page = repositorioPautaJpa.findAll(spec, pageable);

        List<PautaDetalhadaRespostaDTO> conteudo = page.getContent().stream()
                .map(mapper::paraDetalhadaRespostaDTO)
                .toList();

        return new PaginaDTO<>(conteudo, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public EstatisticasDashboardDTO buscarEstatisticas(FiltroPautaDTO filtro) {
        log.debug("Adaptador: Buscando estatísticas com filtros: {}", filtro);

        Specification<br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade> specBase = construirEspecificacaoBase(filtro);

        long totalPautas = repositorioPautaJpa.count(specBase);

        long pautasAbertas = repositorioPautaJpa.count(specBase.and((root, query, cb) -> {
            var sessaoJoin = root.join("sessao", JoinType.INNER);
            return cb.equal(sessaoJoin.get("status"), SessaoStatus.ABERTA);
        }));

        long pautasEncerradas = repositorioPautaJpa.count(specBase.and((root, query, cb) -> {
            var sessaoJoin = root.join("sessao", JoinType.INNER);
            return cb.equal(sessaoJoin.get("status"), SessaoStatus.ENCERRADA);
        }));

        long pautasAguardando = totalPautas - (pautasAbertas + pautasEncerradas);

        long pautasAprovadas = repositorioPautaJpa.count(specBase.and((root, query, cb) -> {
            var sessaoJoin = root.join("sessao", JoinType.INNER);
            return cb.equal(sessaoJoin.get("resultado"), SessaoResultado.APROVADA);
        }));

        long pautasReprovadas = repositorioPautaJpa.count(specBase.and((root, query, cb) -> {
            var sessaoJoin = root.join("sessao", JoinType.INNER);
            return cb.equal(sessaoJoin.get("resultado"), SessaoResultado.REPROVADA);
        }));

        long pautasEmpatadas = repositorioPautaJpa.count(specBase.and((root, query, cb) -> {
            var sessaoJoin = root.join("sessao", JoinType.INNER);
            return cb.equal(sessaoJoin.get("resultado"), SessaoResultado.EMPATE);
        }));

        long pautasSemVotos = repositorioPautaJpa.count(specBase.and((root, query, cb) -> {
            var sessaoJoin = root.join("sessao", JoinType.INNER);
            return cb.equal(sessaoJoin.get("resultado"), SessaoResultado.SEM_VOTOS);
        }));

        return new EstatisticasDashboardDTO(
            totalPautas, 
            pautasAguardando, 
            pautasAbertas, 
            pautasEncerradas,
            pautasAprovadas,
            pautasReprovadas,
            pautasEmpatadas,
            pautasSemVotos
        );
    }

    private Specification<br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade> construirEspecificacaoBase(FiltroPautaDTO filtro) {
        Specification<br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade> spec = Specification.where(null);

        if (filtro.periodo() != null) {
            Instant dataInicio = calcularDataInicio(filtro.periodo());
            if (dataInicio != null) {
                spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataHoraCriacao"), dataInicio));
            }
        }

        if (filtro.statusSessao() != null) {
            SessaoStatus status = SessaoStatus.valueOf(filtro.statusSessao());
            spec = spec.and((root, query, cb) -> {
                var sessaoJoin = root.join("sessao", JoinType.LEFT);

                if (status == SessaoStatus.AGUARDANDO) {
                    return cb.isNull(sessaoJoin.get("id"));
                }
                return cb.equal(sessaoJoin.get("status"), status);
            });
        }

        if (filtro.resultado() != null) {
            SessaoResultado resultado = SessaoResultado.valueOf(filtro.resultado());
            spec = spec.and((root, query, cb) -> {
                var sessaoJoin = root.join("sessao", JoinType.LEFT);
                return cb.equal(sessaoJoin.get("resultado"), resultado);
            });
        }

        return spec;
    }

    private Instant calcularDataInicio(PeriodoFiltroEnum periodo) {
        if (periodo == null) {
            return null;
        }
        LocalDate hoje = LocalDate.now();
        return switch (periodo) {
            case HOJE -> hoje.atStartOfDay(ZoneOffset.UTC).toInstant();
            case ULTIMO_7_DIAS -> hoje.minusDays(7).atStartOfDay(ZoneOffset.UTC).toInstant();
            case ULTIMOS_15_DIAS -> hoje.minusDays(15).atStartOfDay(ZoneOffset.UTC).toInstant();
            case ULTIMO_MES -> hoje.minusMonths(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            case ULTIMO_3_MESES -> hoje.minusMonths(3).atStartOfDay(ZoneOffset.UTC).toInstant();
            case ULTIMO_6_MESES -> hoje.minusMonths(6).atStartOfDay(ZoneOffset.UTC).toInstant();
            case ULTIMO_ANO -> hoje.minusYears(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            case TODO_PERIODO -> null;
        };
    }
}