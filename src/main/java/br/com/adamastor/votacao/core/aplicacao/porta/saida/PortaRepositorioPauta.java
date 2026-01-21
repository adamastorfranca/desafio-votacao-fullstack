package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.dominio.modelo.Pauta;

public interface PortaRepositorioPauta {

    Pauta salvar(Pauta pauta);

}