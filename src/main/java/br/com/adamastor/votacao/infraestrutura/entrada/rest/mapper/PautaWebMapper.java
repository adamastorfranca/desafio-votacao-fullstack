package br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosCriacaoPautaDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.CriarPautaRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.PautaRespostaDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PautaWebMapper {

    DadosCriacaoPautaDTO paraDtoAplicacao(CriarPautaRequisicaoDTO requisicao);

    PautaRespostaDTO paraResposta(Pauta pauta);

}