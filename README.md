# Microsserviço de Projetos

Microsserviço do **Portal de Cursos** responsável pelo gerenciamento de **Projetos Acadêmicos**, incluindo operações de cadastro, consulta, atualização e exclusão, além do gerenciamento do fluxo de análise e publicação.

A API REST utiliza o prefixo `/api/projetos` para seus endpoints.

## Tecnologias

| Tecnologia | Versão | Para que serve |
|---|---|---|
| Java | 21 | Linguagem de programação |
| Spring Boot | 4.1.1 | Framework principal |
| Spring Data JPA | - | Acesso e persistência de dados |
| Spring Validation | - | Validação dos dados de entrada |
| Spring Web MVC | - | Desenvolvimento da API REST |
| PostgreSQL | - | Banco de dados relacional |
| Flyway | - | Gerenciamento de migrations do banco |
| MapStruct | 1.6.3 | Conversão entre Entity e DTO |
| Lombok | - | Redução de código repetitivo |
| SpringDoc OpenAPI | 3.1.0 | Documentação da API via Swagger |

## Como rodar

### 1. Crie o banco de dados

No PostgreSQL (pgAdmin), crie um banco de dados vazio.

As tabelas e demais estruturas necessárias serão criadas automaticamente pelo Flyway ao iniciar a aplicação.

### 2. Configure o arquivo `.env`

Copie o arquivo `.env.example` para `.env`, na raiz do projeto, e configure as variáveis de ambiente:

```env
DB_URL=jdbc:postgresql://localhost:5432/nome_do_banco
DB_USERNAME=usuario
DB_PASSWORD=senha
SERVER_PORT=8083
APP_STORAGE_DIR=portalcursos/projetos
```

Substitua `nome_do_banco`, `usuario` e `senha` pelas informações do seu ambiente.

A variável `APP_STORAGE_DIR` define o diretório utilizado para o armazenamento dos documentos dos projetos. Caso não seja informada, será utilizado o diretório padrão `portalcursos/projetos`.

### 3. Configure o IntelliJ para ler o `.env`

O Spring Boot não carrega automaticamente arquivos `.env`. Para configurar o IntelliJ:

1. Instale o plugin **EnvFile** em `Settings > Plugins`.
2. Acesse `Run > Edit Configurations` e selecione `ProjectsApplication`.
3. Marque a opção **Enable EnvFile**.
4. Clique em **+**, escolha **.env file** e selecione o arquivo `.env` do projeto.
5. Clique em **Apply** e execute a aplicação.

### 4. Acesse a documentação Swagger

Com a aplicação em execução, a documentação interativa da API estará disponível em:

[http://localhost:8083/api/projetos/swagger-ui/index.html](http://localhost:8083/api/projetos/swagger-ui/index.html)

## API de Projetos

Os endpoints da API REST utilizam o prefixo `/api/projetos`.

| Método HTTP | Endpoint | Descrição |
|---|---|---|
| POST | `/api/projetos/projetos` | Cadastrar um projeto |
| GET | `/api/projetos/projetos/{id}` | Consultar um projeto por ID |
| GET | `/api/projetos/projetos` | Listar projetos |
| PUT | `/api/projetos/projetos/{id}` | Atualizar um projeto |
| PATCH | `/api/projetos/projetos/{id}/status` | Atualizar o status de um projeto |
| DELETE | `/api/projetos/projetos/{id}` | Excluir um projeto |

> **Observação:** O prefixo `/api/projetos` é definido pela propriedade `server.servlet.context-path`. O segmento `/projetos` após esse prefixo corresponde ao mapeamento do controller.

## Armazenamento de documentos

O microsserviço possui configuração para armazenamento de documentos relacionados aos projetos.

| Propriedade | Valor padrão | Descrição |
|---|---|---|
| `app.storage.diretorio` | `portalcursos/projetos` | Diretório de armazenamento dos documentos |
| `spring.servlet.multipart.max-file-size` | `10MB` | Tamanho máximo permitido por arquivo |
| `spring.servlet.multipart.max-request-size` | `12MB` | Tamanho máximo permitido por requisição multipart |

O diretório de armazenamento pode ser personalizado por meio da variável de ambiente `APP_STORAGE_DIR`.

## Migrations (Flyway)

Os scripts SQL ficam em `src/main/resources/db/migration/`, seguindo o padrão de nomenclatura do Flyway:

```text
V1__create_table_projetos.sql
V2__create_table_autor_projeto.sql
V3__create_table_projeto_disciplina.sql
V4__create_table_projeto_professor.sql
V5__create_table_projeto_documento.sql
V6__nome_da_alteracao.sql
```

### Regras para migrations

- **Não editar** uma migration que já foi aplicada ao banco. Crie uma nova migration (`V6`, `V7` etc.).
- **Não criar tabelas manualmente pelo pgAdmin.** Todas as alterações estruturais do banco devem ser realizadas por meio de migrations.
- O Hibernate está configurado com `ddl-auto: validate`, portanto apenas valida se as entidades estão de acordo com a estrutura do banco de dados, sem criar ou modificar tabelas automaticamente.