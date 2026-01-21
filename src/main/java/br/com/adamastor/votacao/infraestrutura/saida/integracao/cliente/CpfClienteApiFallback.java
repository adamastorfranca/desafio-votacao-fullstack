package br.com.adamastor.votacao.infraestrutura.saida.integracao.cliente;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CpfClienteApiFallback implements CpfClienteApi {

    @Override
    public RespostaCpf buscarStatusCpf(String cpf) {
        log.warn("Serviço de validação de CPF indisponível. CPF: {}. Permitindo votação como fallback.", cpf);
        return RespostaCpf.builder()
                .status("ABLE_TO_VOTE")
                .build();
    }

}

