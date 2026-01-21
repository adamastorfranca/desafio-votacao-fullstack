package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;

import java.util.UUID;

public interface PortaRepositorioVoto {

    Voto salvar(Voto voto);

    boolean existeVotoDoAssociadoNaSessao(UUID sessaoId, String cpfAssociado);

}

