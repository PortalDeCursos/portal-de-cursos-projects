CREATE TABLE projetos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    curso_id BIGINT NOT NULL,
    orientador_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    tipo_projeto VARCHAR(30) NOT NULL,
    status_projeto VARCHAR(20) NOT NULL DEFAULT 'EM_ANALISE',
    arquivo_pdf VARCHAR(500),

    CONSTRAINT chk_tipo_projetos
        CHECK (tipo_projeto IN (
            'TCC',
            'PROJETO_INTEGRADOR',
            'PROJETO_PESQUISA',
            'PROJETO_EXTENSAO'
        )),

    CONSTRAINT chk_status_projeto
        CHECK (status_projeto IN (
            'EM_ANALISE',
            'APROVADO',
            'RECUSADO'
        ))
);
