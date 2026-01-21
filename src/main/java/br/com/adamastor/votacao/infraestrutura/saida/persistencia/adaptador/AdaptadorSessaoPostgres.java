package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.SessaoPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioSessaoJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorSessaoPostgres implements PortaRepositorioSessao {

    private final RepositorioSessaoJpa repositorioSessaoJpa;
    private final SessaoPersistenciaMapper mapper;

    @Override
    public Sessao salvar(Sessao sessao) {
        log.debug("Adaptador: Persistindo sessão para pauta: {}", sessao.getPautaId());

        var entidade = mapper.paraEntidade(sessao);
        if (entidade.getId() == null) {
            entidade.setId(UUID.randomUUID());
        }

        var entidadeSalva = repositorioSessaoJpa.save(entidade);

        log.debug("Adaptador: Sessão persistida com ID: {}", entidadeSalva.getId());

        return mapper.paraDominio(entidadeSalva);
    }

    @Override
    public boolean existeSessaoAbertaParaPauta(UUID pautaId) {
        return repositorioSessaoJpa.existsByPautaIdAndStatus(pautaId, SessaoStatus.ABERTA);
    }

    @Override
    public Optional<Sessao> obterPorId(UUID sessaoId) {
        log.debug("Adaptador: Buscando sessão com ID: {}", sessaoId);

        return repositorioSessaoJpa.findById(sessaoId)
                .map(mapper::paraDominio);
    }

    @Override
    public List<Sessao> buscarSessoesEncerradasNaoContabilizadas() {
        return List.of();
    }
}