
CREATE TABLE projeto_professor (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    projeto_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    papel VARCHAR(30) NOT NULL,

    CONSTRAINT fk_projeto_professor_projeto
        FOREIGN KEY (projeto_id)
        REFERENCES projetos(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_projeto_professor_papel
        CHECK (papel IN ('ORIENTADOR', 'PARTICIPANTE')),

    CONSTRAINT uk_projeto_professor
        UNIQUE (projeto_id, professor_id)
);

CREATE INDEX idx_projeto_professor_professor_id
    ON projeto_professor(professor_id);

CREATE UNIQUE INDEX uk_projeto_professor_orientador
    ON projeto_professor(projeto_id)
    WHERE papel = 'ORIENTADOR';
