package br.com.adamastor.votacao.infraestrutura.entrada.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstantes {

    private static final String API = "/api";
    private static final String API_V1 = API + "/v1";

    public static final String PATH_PAUTAS = "/pautas";
    public static final String PATH_SESSOES = "/sessoes";

    public static final String ROTA_PAUTAS_V1 = API_V1 + PATH_PAUTAS;
    public static final String ROTA_SESSOES_V1 = API_V1 + PATH_SESSOES;

    public static final String RECURSO_VOTOS_V1 = ROTA_SESSOES_V1 + "/{sessaoId}/votos";

}