package br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio;

import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.SessaoEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepositorioSessaoJpa extends JpaRepository<SessaoEntidade, UUID> {

    boolean existsByPautaIdAndStatus(UUID pautaId, SessaoStatus status);

}