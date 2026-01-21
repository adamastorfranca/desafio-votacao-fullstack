package br.com.adamastor.votacao.infraestrutura.configuracao.bean;

import br.com.adamastor.votacao.infraestrutura.saida.integracao.cliente.CpfClienteApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ConfiguracaoIntegracao {

    @Bean
    public CpfClienteApi cpfClienteApi(RestClient.Builder builder,
                                       @Value("${integracao.cpf.url}") String url) {
        RestClient restClient = builder.baseUrl(url).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(CpfClienteApi.class);
    }
}
