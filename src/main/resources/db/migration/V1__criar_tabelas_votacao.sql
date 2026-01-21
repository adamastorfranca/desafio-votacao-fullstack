CREATE TABLE tb_pauta (
    id_pauta UUID NOT NULL,
    tx_titulo VARCHAR(255) NOT NULL,
    tx_descricao TEXT,
    dh_criacao TIMESTAMP NOT NULL,
    CONSTRAINT pk_pauta PRIMARY KEY (id_pauta)
);

CREATE TABLE tb_sessao (
    id_sessao UUID NOT NULL,
    id_pauta UUID NOT NULL,
    dh_inicio TIMESTAMP,
    dh_termino TIMESTAMP,
    st_situacao VARCHAR(20) NOT NULL,
    nr_total_votos INTEGER DEFAULT 0,
    nr_total_sim INTEGER DEFAULT 0,
    nr_total_nao INTEGER DEFAULT 0,
    tx_opcao_ganhadora VARCHAR(20),
    CONSTRAINT pk_sessao PRIMARY KEY (id_sessao),
    CONSTRAINT fk_sessao_pauta FOREIGN KEY (id_pauta) REFERENCES tb_pauta(id_pauta)
);

CREATE TABLE tb_voto (
    id_voto UUID NOT NULL,
    id_sessao UUID NOT NULL,
    tx_cpf_associado VARCHAR(14) NOT NULL,
    tx_opcao_voto VARCHAR(3) NOT NULL,
    dh_criacao TIMESTAMP NOT NULL,
    CONSTRAINT pk_voto PRIMARY KEY (id_voto),
    CONSTRAINT fk_voto_sessao FOREIGN KEY (id_sessao) REFERENCES tb_sessao(id_sessao),
    CONSTRAINT uk_voto_sessao_cpf UNIQUE (id_sessao, tx_cpf_associado)
);