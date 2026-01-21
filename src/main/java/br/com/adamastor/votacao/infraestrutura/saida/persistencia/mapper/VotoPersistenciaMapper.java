package br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VotoPersistenciaMapper {

    VotoEntidade paraEntidade(Voto voto);

    Voto paraDominio(VotoEntidade entidade);

}

