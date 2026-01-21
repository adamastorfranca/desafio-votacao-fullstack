package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import java.util.UUID;

public interface PortaRepositorioSessao {

    Sessao salvar(Sessao sessao);

    boolean existeSessaoAbertaParaPauta(UUID pautaId);

}