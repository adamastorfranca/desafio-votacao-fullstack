package br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosAberturaSessaoDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.AbrirSessaoRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.SessaoRespostaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessaoWebMapper {

    DadosAberturaSessaoDTO paraDtoAplicacao(AbrirSessaoRequisicaoDTO requisicao);

    @Mapping(target = "status", source = "status.descricao")
    SessaoRespostaDTO paraResposta(Sessao sessao);

}
