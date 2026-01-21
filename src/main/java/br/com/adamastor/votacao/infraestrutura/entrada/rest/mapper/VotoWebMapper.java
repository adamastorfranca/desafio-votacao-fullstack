package br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosVotoDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.RegistrarVotoRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.VotoRespostaDTO;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VotoWebMapper {

    default DadosVotoDTO paraDtoAplicacao(RegistrarVotoRequisicaoDTO requisicao, UUID sessaoId) {
        return new DadosVotoDTO(sessaoId, requisicao.cpfAssociado(), requisicao.opcao());
    }

    VotoRespostaDTO paraResposta(Voto voto);

}

