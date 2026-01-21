package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.PautaPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioPautaJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}