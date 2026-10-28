CREATE TABLE projeto_disciplina (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    projeto_id BIGINT NOT NULL,
    disciplina_id BIGINT NOT NULL,

    CONSTRAINT fk_projeto_disciplina_projeto
        FOREIGN KEY (projeto_id)
        REFERENCES projetos(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_projeto_disciplina
        UNIQUE (projeto_id, disciplina_id)
);

CREATE INDEX idx_projeto_disciplina_disciplina_id
    ON projeto_disciplina(disciplina_id);