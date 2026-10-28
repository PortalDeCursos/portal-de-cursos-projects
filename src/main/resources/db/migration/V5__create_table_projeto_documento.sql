CREATE TABLE projeto_documento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    projeto_id BIGINT NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_documento VARCHAR(50) NOT NULL,
    localizacao VARCHAR(500) NOT NULL,
    data_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_projeto_documento_projeto
        FOREIGN KEY (projeto_id)
        REFERENCES projetos(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_projeto_documento_projeto_id
    ON projeto_documento(projeto_id);
