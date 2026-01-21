package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.VotoPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioVotoJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorVotoPostgres implements PortaRepositorioVoto {

    private final RepositorioVotoJpa repositorioVotoJpa;
    private final VotoPersistenciaMapper mapper;

    @Override
    public Voto salvar(Voto voto) {
        log.debug("Adaptador: Persistindo voto para sessão: {}, CPF: {}", voto.getSessaoId(), voto.getCpfAssociado());

        var entidade = mapper.paraEntidade(voto);
        var entidadeSalva = repositorioVotoJpa.save(entidade);

        log.debug("Adaptador: Voto persistido com ID: {}", entidadeSalva.getId());

        return mapper.paraDominio(entidadeSalva);
    }

    @Override
    public boolean existeVotoDoAssociadoNaSessao(UUID sessaoId, String cpfAssociado) {
        log.debug("Adaptador: Verificando se existe voto do CPF {} na sessão {}", cpfAssociado, sessaoId);

        return repositorioVotoJpa.existsBySessaoIdAndCpfAssociado(sessaoId, cpfAssociado);
    }

    @Override
    public List<ContagemVotosDTO> contarVotosPorOpcao(UUID sessaoId) {
        log.debug("Adaptador: Realizando contagem agregada de votos para sessão: {}", sessaoId);

        return repositorioVotoJpa.contarVotosAgrupadosPorOpcao(sessaoId);
    }

}

