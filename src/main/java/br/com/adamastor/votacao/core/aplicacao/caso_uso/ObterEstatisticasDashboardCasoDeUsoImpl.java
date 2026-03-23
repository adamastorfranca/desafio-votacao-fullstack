package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.EstatisticasDashboardDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.FiltroPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ObterEstatisticasDashboardCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObterEstatisticasDashboardCasoDeUsoImpl implements ObterEstatisticasDashboardCasoDeUso {

    private final PortaRepositorioPauta portaRepositorioPauta;

    @Override
    public EstatisticasDashboardDTO executar(FiltroPautaDTO filtro) {
        log.info("Caso de Uso: Iniciando obtenção de estatísticas do dashboard com filtro: {}", filtro);

        var estatisticas = portaRepositorioPauta.buscarEstatisticas(filtro);

        log.info("Caso de Uso: Estatísticas obtidas com sucesso. Total pautas: {}, Aguardando: {}, Abertas: {}, Encerradas: {}",
                 estatisticas.totalPautas(), estatisticas.pautasAguardando(), estatisticas.pautasAbertas(), estatisticas.pautasEncerradas());

        return estatisticas;
    }
}
