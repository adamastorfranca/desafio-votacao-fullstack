package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;

import java.util.List;
import java.util.UUID;

public interface PortaRepositorioVoto {

    Voto salvar(Voto voto);

    boolean existeVotoDoAssociadoNaSessao(UUID sessaoId, String cpfAssociado);

    List<ContagemVotosDTO> contarVotosPorOpcao(UUID sessaoId);

}

