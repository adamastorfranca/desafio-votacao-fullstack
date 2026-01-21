package br.com.adamastor.votacao.infraestrutura.configuracao.bean;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class ConfiguracaoKafka {

    public static final String TOPICO_VOTOS = "votos-registrados-topic";

    @Bean
    public NewTopic topicoVotosRegistrados() {
        return TopicBuilder.name(TOPICO_VOTOS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}