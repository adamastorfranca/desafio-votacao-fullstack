package br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio;

import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepositorioVotoJpa extends JpaRepository<VotoEntidade, UUID> {

    boolean existsBySessaoIdAndCpfAssociado(UUID sessaoId, String cpfAssociado);

}

