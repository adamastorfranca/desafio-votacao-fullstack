package br.com.adamastor.votacao.infraestrutura.saida.mensageria.adaptador;

import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorVoto;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.infraestrutura.configuracao.bean.ConfiguracaoKafka;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.mapper.VotoMensagemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdaptadorPublicadorVotoKafka implements PortaPublicadorVoto {

    private final KafkaTemplate<String, VotoMensagemDTO> kafkaTemplate;
    private final VotoMensagemMapper mapper;

    @Override
    public void publicar(Voto voto) {
        var mensagem = mapper.paraMensagem(voto);

        kafkaTemplate.send(ConfiguracaoKafka.TOPICO_VOTOS, voto.getId().toString(), mensagem)
                .whenComplete((resultado, ex) -> {
                    if (ex != null) {
                        log.error("Erro ao publicar voto no Kafka. ID: {}", voto.getId(), ex);
                        throw new RuntimeException("Falha ao enviar voto para processamento", ex);
                    }
                });
    }
}