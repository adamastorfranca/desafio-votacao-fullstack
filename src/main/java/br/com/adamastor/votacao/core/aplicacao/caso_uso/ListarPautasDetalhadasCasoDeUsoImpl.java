package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.FiltroPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginaDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PaginacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.PautaDetalhadaRespostaDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.entrada.ListarPautasDetalhadasCasoDeUso;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListarPautasDetalhadasCasoDeUsoImpl implements ListarPautasDetalhadasCasoDeUso {

    private final PortaRepositorioPauta portaRepositorioPauta;

    @Override
    public PaginaDTO<PautaDetalhadaRespostaDTO> executar(FiltroPautaDTO filtro, PaginacaoDTO paginacao) {
        log.info("Caso de Uso: Iniciando listagem de pautas detalhadas com filtro: {} e paginação: {}", filtro, paginacao);

        var pagina = portaRepositorioPauta.buscarComFiltros(filtro, paginacao);

        log.info("Caso de Uso: Pautas detalhadas listadas com sucesso. Total elementos: {}, Página: {}/{}",
                 pagina.totalElementos(), pagina.numeroPagina() + 1, pagina.totalPaginas());

        return pagina;
    }
}
