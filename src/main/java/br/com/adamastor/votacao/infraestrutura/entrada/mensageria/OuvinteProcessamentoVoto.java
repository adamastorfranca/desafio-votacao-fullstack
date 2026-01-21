package br.com.adamastor.votacao.infraestrutura.entrada.mensageria;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import br.com.adamastor.votacao.infraestrutura.configuracao.bean.ConfiguracaoKafka;
import br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioVotoJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OuvinteProcessamentoVoto {

    private final RepositorioVotoJpa repositorioVotoJpa;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(
            topics = ConfiguracaoKafka.TOPICO_VOTOS,
            groupId = "grupo-processamento-votos",
            batch = "true",
            properties = {"spring.json.value.default.type=br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto.VotoMensagemDTO"}
    )
    public void processarLoteVotos(List<VotoMensagemDTO> mensagens, Acknowledgment ack) {
        log.info("Processando lote de {} votos recebidos do Kafka", mensagens.size());

        try {
            if (mensagens.isEmpty()) {
                log.debug("Lote vazio recebido do Kafka");
                if (ack != null) {
                    ack.acknowledge();
                }
                return;
            }

            var entidades = mensagens.stream()
                    .map(msg -> {
                        log.debug("Convertendo mensagem de voto. ID: {}, Sessão: {}, CPF: {}", msg.id(), msg.sessaoId(), msg.cpfAssociado());
                        return VotoEntidade.builder()
                                .id(msg.id())
                                .sessaoId(msg.sessaoId())
                                .cpfAssociado(msg.cpfAssociado())
                                .opcao(VotoOpcao.aPartirDe(msg.opcao()))
                                .dataHoraCriacao(msg.dataHoraCriacao())
                                .build();
                    })
                    .toList();

            repositorioVotoJpa.saveAll(entidades);
            repositorioVotoJpa.flush();

            log.info("Lote de {} votos persistido com sucesso no banco de dados", entidades.size());

            if (ack != null) {
                ack.acknowledge();
                log.debug("Acknowledgment enviado para o Kafka");
            }
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha ao persistir votos devido a constraint violation (provável: sessão não encontrada): {}", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            if (ack != null) {
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("Erro inesperado ao processar lote de votos do Kafka", e);
            if (ack != null) {
                ack.nack(Duration.ZERO);
            }
            throw e;
        }
    }

}