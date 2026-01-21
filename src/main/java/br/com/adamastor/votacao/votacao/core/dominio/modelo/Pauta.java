package br.com.adamastor.votacao.core.dominio.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Pauta {

    @EqualsAndHashCode.Include
    private UUID id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataHoraCriacao;

}