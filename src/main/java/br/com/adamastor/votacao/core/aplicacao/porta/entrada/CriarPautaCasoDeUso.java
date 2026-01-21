package br.com.adamastor.votacao.core.aplicacao.porta.entrada;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosCriacaoPautaDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;

public interface CriarPautaCasoDeUso {

    Pauta executar(DadosCriacaoPautaDTO dados);

}