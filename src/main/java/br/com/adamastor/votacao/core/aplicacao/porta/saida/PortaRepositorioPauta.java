package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.dominio.modelo.Pauta;

import java.util.UUID;

public interface PortaRepositorioPauta {

    Pauta salvar(Pauta pauta);

    boolean existePorId(UUID id);

}