package br.com.adamastor.votacao.infraestrutura.entrada.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstantes {

    public static final String API_V1 = "/v1";
    public static final String ROTA_PAUTAS = API_V1 + "/pautas";
    public static final String ROTA_SESSOES = API_V1 + "/sessoes";

}