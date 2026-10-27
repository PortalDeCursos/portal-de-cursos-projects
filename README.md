# Microsserviço de Projetos

Microsserviço do **Portal de Cursos** responsável pelo CRUD de **Projetos Acadêmicos**, incluindo o gerenciamento de autores, disciplinas e fluxo de análise e publicação.

## Tecnologias

| Tecnologia | Versão | Para que serve |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 4.1.1 | Framework principal |
| Spring Data JPA | - | Acesso ao banco |
| Spring Validation | - | Validação dos dados de entrada |
| Spring Web MVC | - | API REST |
| PostgreSQL | - | Banco de dados |
| Flyway | - | Migrations do banco |
| MapStruct | 1.6.3 | Conversão entre Entity e DTO |
| Lombok | - | Redução de código repetitivo |
| SpringDoc OpenAPI | 3.1.0 | Documentação Swagger |

## Como rodar

### 1. Crie o banco

No PostgreSQL (pgAdmin), crie um banco **vazio**. As tabelas são criadas automaticamente pelo Flyway ao subir a aplicação.

### 2. Configure o `.env`

Copie o arquivo `.env.example` para `.env` na raiz do projeto e preencha com suas credenciais:

```env
DB_URL=jdbc:postgresql://localhost:5432/nome_do_banco
DB_USERNAME=usuario
DB_PASSWORD=senha
SERVER_PORT=8082
```

Substitua `nome_do_banco`, `usuario` e `senha` pelos dados do seu ambiente.

### 3. Faça o IntelliJ ler o `.env`

O Spring não lê o `.env` sozinho. No IntelliJ:

1. Instale o plugin **EnvFile** (*Settings > Plugins*).
2. Vá em *Run > Edit Configurations* e selecione `ProjectsApplication`.
3. Marque **Enable EnvFile**.
4. Clique em **+**, escolha **.env file** e selecione o `.env` do projeto.
5. Clique em **Apply** e rode a aplicação.

### 4. Acesse o Swagger

Com a aplicação rodando: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

## Migrations (Flyway)

Os scripts SQL ficam em `src/main/resources/db/migration/`, no padrão:

```text
V1__create_table_projetos.sql
V2__create_table_autor_projeto.sql
V3__create_table_projeto_disciplina.sql
V4__nome_da_alteracao.sql
```

- **Não editar** uma migration que já foi aplicada. Crie uma nova (`V2`, `V3`...).
- **Não criar tabelas pelo pgAdmin.** Toda alteração no banco passa por uma migration.
- O Hibernate está com `ddl-auto: validate`: ele apenas confere se as entidades batem com o banco.
