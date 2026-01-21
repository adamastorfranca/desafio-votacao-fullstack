package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.aplicacao.dto.ResultadoVotacaoDTO;
import java.util.UUID;

public interface PortaPublicadorResultado {
    void publicar(UUID sessaoId, ResultadoVotacaoDTO resultado);
}