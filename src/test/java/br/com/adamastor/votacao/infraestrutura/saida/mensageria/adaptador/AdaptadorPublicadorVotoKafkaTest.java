package br.com.adamastor.votacao.infraestrutura.saida.mensageria.adaptador;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import br.com.adamastor.votacao.infraestrutura.configuracao.bean.ConfiguracaoKafka;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.mapper.VotoMensagemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdaptadorPublicadorVotoKafkaTest {

    @InjectMocks
    private AdaptadorPublicadorVotoKafka adaptador;

    @Mock
    private KafkaTemplate<String, VotoMensagemDTO> kafkaTemplate;

    @Mock
    private VotoMensagemMapper mapper;

    @Test
    void devePublicarVotoComSucesso() {
        var voto = Voto.builder()
                .id(UUID.randomUUID())
                .sessaoId(UUID.randomUUID())
                .cpfAssociado("12345678900")
                .dataHoraCriacao(Instant.now())
                .opcao(VotoOpcao.aPartirDe("Sim"))
                .build();

        var mensagemDTO = new VotoMensagemDTO(
                voto.getId(),
                voto.getSessaoId(),
                voto.getCpfAssociado(),
                "Sim",
                voto.getDataHoraCriacao()
        );

        when(mapper.paraMensagem(voto)).thenReturn(mensagemDTO);

        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        adaptador.publicar(voto);

        verify(mapper).paraMensagem(voto);
        verify(kafkaTemplate).send(
                eq(ConfiguracaoKafka.TOPICO_VOTOS),
                eq(voto.getId().toString()),
                eq(mensagemDTO)
        );
    }
}