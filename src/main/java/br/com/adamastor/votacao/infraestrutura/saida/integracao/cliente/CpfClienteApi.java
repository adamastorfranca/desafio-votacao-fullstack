package br.com.adamastor.votacao.infraestrutura.saida.integracao.cliente;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CpfClienteApi {

    @GetMapping("/users/{cpf}")
    RespostaCpf buscarStatusCpf(@PathVariable String cpf);

}

