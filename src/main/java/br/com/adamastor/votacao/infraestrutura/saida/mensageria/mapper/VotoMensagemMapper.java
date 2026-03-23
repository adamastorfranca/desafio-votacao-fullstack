package br.com.adamastor.votacao.infraestrutura.saida.mensageria.mapper;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VotoMensagemMapper {

    @Mapping(target = "opcao", expression = "java(voto.getOpcao().name())")
    VotoMensagemDTO paraMensagem(Voto voto);
}
