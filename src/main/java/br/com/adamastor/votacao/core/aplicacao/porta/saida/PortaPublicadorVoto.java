package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;

public interface PortaPublicadorVoto {

    void publicar(Voto voto);

}
