package br.com.adamastor.votacao.infraestrutura.saida.mensageria.adaptador;

import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorVoto;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.mapper.VotoMensagemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorPublicadorVotoKafka implements PortaPublicadorVoto {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final VotoMensagemMapper mapper;

    @Value("${integracao.kafka.topic-votos}")
    private String topicoVotos;

    @Override
    public void publicar(Voto voto) {
        var mensagem = mapper.paraMensagem(voto);
        var chave = voto.getId().toString();

        log.debug("Publicando voto no Kafka. Tópico: {}, Chave: {}", topicoVotos, chave);

        kafkaTemplate.send(topicoVotos, chave, mensagem)
                .whenComplete((resultado, ex) -> {
                    if (ex != null) {
                        log.error("Erro ao publicar voto no Kafka. ID: {}", voto.getId(), ex);
                        throw new RuntimeException("Falha ao enviar voto para processamento", ex);
                    }
                });
    }
}