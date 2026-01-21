package br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper;

import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.SessaoEntidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessaoPersistenciaMapper {

    @Mapping(target = "resultado", source = "opcaoGanhadora")
    SessaoEntidade paraEntidade(Sessao sessao);

    @Mapping(target = "opcaoGanhadora", source = "resultado")
    Sessao paraDominio(SessaoEntidade entidade);

}