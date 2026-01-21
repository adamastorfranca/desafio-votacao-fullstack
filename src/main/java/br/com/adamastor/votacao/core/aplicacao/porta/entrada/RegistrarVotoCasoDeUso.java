package br.com.adamastor.votacao.core.aplicacao.porta.entrada;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosVotoDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;

public interface RegistrarVotoCasoDeUso {

    Voto executar(DadosVotoDTO dados);

}
