package br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio;

import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepositorioPautaJpa extends JpaRepository<PautaEntidade, UUID>, JpaSpecificationExecutor<PautaEntidade> {

}
