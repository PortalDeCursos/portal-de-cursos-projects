
CREATE TABLE autor_projeto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    projeto_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,

    CONSTRAINT fk_autor_projeto_projeto
        FOREIGN KEY (projeto_id)
        REFERENCES projetos(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_autor_projeto_projeto_id
    ON autor_projeto(projeto_id);
