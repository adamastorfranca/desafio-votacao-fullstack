package br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper;

import br.com.adamastor.votacao.core.aplicacao.dto.PautaDetalhadaRespostaDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PautaPersistenciaMapper {

    PautaEntidade paraEntidade(Pauta pauta);

    Pauta paraDominio(PautaEntidade pautaEntidade);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "descricao", source = "descricao")
    @Mapping(target = "dataHoraCriacao", source = "dataHoraCriacao")
    @Mapping(target = "idSessao", source = "sessao.id")
    @Mapping(target = "statusSessao", source = "sessao.status", defaultValue = "AGUARDANDO")
    @Mapping(target = "dataHoraInicio", source = "sessao.dataHoraInicio")
    @Mapping(target = "dataHoraTermino", source = "sessao.dataHoraTermino")
    @Mapping(target = "totalVotos", source = "sessao.totalVotos")
    @Mapping(target = "totalSim", source = "sessao.totalSim")
    @Mapping(target = "totalNao", source = "sessao.totalNao")
    @Mapping(target = "opcaoGanhadora", source = "sessao.resultado")
    PautaDetalhadaRespostaDTO paraDetalhadaRespostaDTO(PautaEntidade entidade);

    default LocalDateTime map(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

}