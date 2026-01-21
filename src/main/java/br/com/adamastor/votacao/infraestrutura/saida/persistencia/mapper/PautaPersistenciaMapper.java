package br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper;

import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PautaPersistenciaMapper {

    PautaEntidade paraEntidade(Pauta pauta);

    Pauta paraDominio(PautaEntidade pautaEntidade);

}