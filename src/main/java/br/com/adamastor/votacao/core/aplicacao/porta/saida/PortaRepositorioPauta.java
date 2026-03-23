package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.aplicacao.dto.EstatisticasDashboardDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.FiltroPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PautaDetalhadaRespostaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginacaoDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;

import java.util.UUID;

public interface PortaRepositorioPauta {

    Pauta salvar(Pauta pauta);

    boolean existePorId(UUID id);

    PaginaDTO<PautaDetalhadaRespostaDTO> buscarComFiltros(FiltroPautaDTO filtro, PaginacaoDTO paginacao);

    EstatisticasDashboardDTO buscarEstatisticas(FiltroPautaDTO filtro);

}
