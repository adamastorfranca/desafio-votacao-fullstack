package br.com.adamastor.votacao.core.aplicacao.porta.entrada;

import br.com.adamastor.votacao.core.aplicacao.dto.FiltroPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PautaDetalhadaRespostaDTO;

public interface ListarPautasDetalhadasCasoDeUso {

    PaginaDTO<PautaDetalhadaRespostaDTO> executar(FiltroPautaDTO filtro, PaginacaoDTO paginacao);

}
