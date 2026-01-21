package br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio;

import br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepositorioVotoJpa extends JpaRepository<VotoEntidade, UUID> {

    boolean existsBySessaoIdAndCpfAssociado(UUID sessaoId, String cpfAssociado);

    @Query("SELECT new br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO(v.opcao, COUNT(v)) " +
            "FROM VotoEntidade v WHERE v.sessaoId = :sessaoId GROUP BY v.opcao")
    List<ContagemVotosDTO> contarVotosAgrupadosPorOpcao(@Param("sessaoId") UUID sessaoId);

}

