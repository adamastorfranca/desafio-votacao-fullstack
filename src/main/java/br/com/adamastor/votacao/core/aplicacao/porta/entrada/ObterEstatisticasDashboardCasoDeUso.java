package br.com.adamastor.votacao.core.aplicacao.porta.entrada;

import br.com.adamastor.votacao.core.aplicacao.dto.EstatisticasDashboardDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.FiltroPautaDTO;

public interface ObterEstatisticasDashboardCasoDeUso {

    EstatisticasDashboardDTO executar(FiltroPautaDTO filtro);

}
