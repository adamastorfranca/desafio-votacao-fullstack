package br.com.adamastor.votacao.infraestrutura.saida.integracao.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaCpf {

    @JsonProperty("status")
    private String status;

}

