package br.com.adamastor.votacao.core.aplicacao.porta.saida;

import br.com.adamastor.votacao.core.dominio.modelo.Sessao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortaRepositorioSessao {

    Sessao salvar(Sessao sessao);

    boolean existeSessaoAbertaParaPauta(UUID pautaId);

    Optional<Sessao> obterPorId(UUID sessaoId);

    List<Sessao> buscarSessoesEncerradasNaoContabilizadas();
}
