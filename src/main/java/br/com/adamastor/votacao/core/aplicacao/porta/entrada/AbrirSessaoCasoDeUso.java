package br.com.adamastor.votacao.core.aplicacao.porta.entrada;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosAberturaSessaoDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;

public interface AbrirSessaoCasoDeUso {

    Sessao executar(DadosAberturaSessaoDTO dados);

}