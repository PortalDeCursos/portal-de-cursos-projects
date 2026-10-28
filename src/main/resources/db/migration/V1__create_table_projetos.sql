
CREATE TABLE projetos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    campus_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL UNIQUE,
    resumo VARCHAR(500) NOT NULL,
    descricao TEXT NOT NULL,
    tipo_projeto VARCHAR(30) NOT NULL,
    status_projeto VARCHAR(30) NOT NULL DEFAULT 'EM_ANALISE',
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,

    CONSTRAINT chk_tipo_projetos
        CHECK (tipo_projeto IN (
            'TCC',
            'PROJETO_INTEGRADOR',
            'PROJETO_PESQUISA',
            'PROJETO_EXTENSAO',
            'OUTROS'
        )),

    CONSTRAINT chk_status_projeto
        CHECK (status_projeto IN (
            'EM_ANALISE',
            'APROVADO',
            'RECUSADO',
            'ANALISE_PUBLICACAO',
            'PUBLICADO',
            'RECUSADO_PUBLICACAO',
            'REVISAO'
        )),

    CONSTRAINT chk_projeto_datas
        CHECK (data_fim >= data_inicio)
);
