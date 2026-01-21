package br.com.adamastor.votacao.infraestrutura.saida.mensageria.adaptador;

import br.com.adamastor.votacao.core.aplicacao.dto.ResultadoVotacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorResultado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorPublicadorResultadoKafka implements PortaPublicadorResultado {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPICO_RESULTADOS = "sessao-resultado-topic";

    @Override
    public void publicar(UUID sessaoId, ResultadoVotacaoDTO resultado) {
        log.info("Publicando resultado da sessão {} no Kafka.", sessaoId);

        try {
            kafkaTemplate.send(TOPICO_RESULTADOS, sessaoId.toString(), resultado)
                    .get();
        } catch (Exception e) {
            log.error("Erro ao publicar resultado da pauta no Kafka. ID Sessão: {}", sessaoId, e);
            throw new RuntimeException("Falha ao enviar resultado para processamento", e);
        }
    }
}