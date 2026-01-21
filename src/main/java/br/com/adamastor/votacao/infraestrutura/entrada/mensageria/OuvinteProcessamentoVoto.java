package br.com.adamastor.votacao.infraestrutura.entrada.mensageria;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import br.com.adamastor.votacao.infraestrutura.configuracao.bean.ConfiguracaoKafka;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioVotoJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OuvinteProcessamentoVoto {

    private final RepositorioVotoJpa repositorioVotoJpa;

    @Transactional
    @KafkaListener(
            topics = ConfiguracaoKafka.TOPICO_VOTOS,
            groupId = "grupo-processamento-votos",
            batch = "true",
            properties = {"spring.json.value.default.type=br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO"}
    )
    public void processarLoteVotos(List<VotoMensagemDTO> mensagens) {
        log.info("Processando lote de {} votos recebidos do Kafka", mensagens.size());

        var entidades = mensagens.stream()
                .map(msg -> VotoEntidade.builder()
                        .id(msg.id())
                        .sessaoId(msg.sessaoId())
                        .cpfAssociado(msg.cpfAssociado())
                        .opcao(VotoOpcao.aPartirDe(msg.opcao())
                        .dataHoraCriacao(msg.dataHoraCriacao())
                        .build())
                .toList();

        repositorioVotoJpa.saveAll(entidades);

        log.info("Lote de {} votos persistido com sucesso no banco de dados", entidades.size());
    }
}