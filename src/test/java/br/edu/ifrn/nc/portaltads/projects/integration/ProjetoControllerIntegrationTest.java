package br.edu.ifrn.nc.portaltads.projects.integration;

import br.edu.ifrn.nc.portaltads.projects.business.dto.*;
import br.edu.ifrn.nc.portaltads.projects.business.messaging.ProjetoEventPublisher;
import br.edu.ifrn.nc.portaltads.projects.client.CampusClient;
import br.edu.ifrn.nc.portaltads.projects.client.CursoClient;
import br.edu.ifrn.nc.portaltads.projects.client.DisciplinaClient;
import br.edu.ifrn.nc.portaltads.projects.client.ProfessorClient;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.AcessoNegadoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.TokenInvalidoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProjetoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjetoRepository projetoRepository;

    @MockitoBean
    private CursoClient cursoClient;

    @MockitoBean
    private CampusClient campusClient;

    @MockitoBean
    private DisciplinaClient disciplinaClient;

    @MockitoBean
    private ProfessorClient professorClient;

    @MockitoBean
    private ProjetoEventPublisher projetoEventPublisher;

    private void configurarIntegracoesExternas() {

        // Simula um campus existente
        when(campusClient.buscarPorID(1L))
                .thenReturn(new CampusDTO(1L));

        // Simula um curso existente
        when(cursoClient.buscarPorID(1L))
                .thenReturn(new CursoDTO(1L, 1L));

        // Simula disciplinas pertencentes ao curso
        when(disciplinaClient.buscarPorCurso(1L))
                .thenReturn(List.of(
                        new DisciplinaDTO(1L, 1L),
                        new DisciplinaDTO(2L, 1L)
                ));

        // Simula um professor existente
        when(professorClient.buscarPorId(eq(10L), anyString()))
                .thenReturn(new ProfessorDTO(
                        10L,
                        new UsuarioDTO(20L, "Professor Teste", "professor@teste.com"),
                        1L
                ));
    }

    @Test
    void deveCadastrarProjetoComSucesso() throws Exception {

        configurarIntegracoesExternas();

        String token = "Bearer token-teste";

        String json = """
            {
                "cursoId": 1,
                "campusId": 1,
                "autores": [
                    {
                        "nome": "Autor Teste",
                        "email": "autor@teste.com"
                    }
                ],
                "disciplinaIds": [1, 2],
                "nome": "Projeto de Integracao Teste",
                "descricao": "Descricao do projeto de integracao",
                "resumo": "Resumo do projeto de integracao",
                "tipoProjeto": "TCC",
                "dataInicio": "2026-11-20",
                "dataFim": "2027-06-20",
                "professores": [
                    {
                        "professorId": 10,
                        "papel": "ORIENTADOR"
                    }
                ]
            }
            """;

        long quantidadeAntes = projetoRepository.count();

        mockMvc.perform(post("/projetos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists());

        assertEquals(quantidadeAntes + 1, projetoRepository.count());

        var projetoSalvo = projetoRepository.findAll().stream()
                .filter(projeto -> projeto.getNome()
                        .equals("Projeto de Integracao Teste"))
                .findFirst()
                .orElseThrow();

        assertEquals("EM_ANALISE", projetoSalvo.getStatusProjeto().name());

        verify(professorClient, times(2)).buscarPorId(10L, token);
    }

    @Test
    void deveRetornarBadRequestQuandoTokenNaoForEnviado() throws Exception {
        String json = """
        {
            "cursoId": 1,
            "campusId": 1,
            "autores": [
                {
                    "nome": "Autor Teste",
                    "email": "autor@teste.com"
                }
            ],
            "disciplinaIds": [1, 2],
            "nome": "Projeto Sem Token",
            "descricao": "Descricao do projeto",
            "resumo": "Resumo do projeto",
            "tipoProjeto": "TCC",
            "dataInicio": "2026-11-20",
            "dataFim": "2027-06-20",
            "professores": [
                {
                    "professorId": 10,
                    "papel": "ORIENTADOR"
                }
            ]
        }
        """;

        mockMvc.perform(post("/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(professorClient);
    }

    @Test
    void deveRetornarUnauthorizedQuandoTokenForInvalido() throws Exception {
        configurarIntegracoesExternas();

        String token = "Bearer token-invalido";

        when(professorClient.buscarPorId(eq(10L), eq(token)))
                .thenThrow(new TokenInvalidoException("Token inválido ou ausente."));

        String json = """
        {
            "cursoId": 1,
            "campusId": 1,
            "autores": [
                {
                    "nome": "Autor Teste",
                    "email": "autor@teste.com"
                }
            ],
            "disciplinaIds": [1, 2],
            "nome": "Projeto Com Token Invalido",
            "descricao": "Descricao do projeto",
            "resumo": "Resumo do projeto",
            "tipoProjeto": "TCC",
            "dataInicio": "2026-11-20",
            "dataFim": "2027-06-20",
            "professores": [
                {
                    "professorId": 10,
                    "papel": "ORIENTADOR"
                }
            ]
        }
        """;

        mockMvc.perform(post("/projetos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Não autenticado"));

        verify(professorClient).buscarPorId(10L, token);
    }

    @Test
    void deveRetornarForbiddenQuandoUsuarioNaoTiverPermissao() throws Exception {
        configurarIntegracoesExternas();

        String token = "Bearer token-sem-permissao";

        when(professorClient.buscarPorId(eq(10L), eq(token)))
                .thenThrow(new AcessoNegadoException(
                        "Você não tem permissão para consultar este professor."
                ));

        String json = """
        {
            "cursoId": 1,
            "campusId": 1,
            "autores": [
                {
                    "nome": "Autor Teste",
                    "email": "autor@teste.com"
                }
            ],
            "disciplinaIds": [1, 2],
            "nome": "Projeto Com Acesso Negado",
            "descricao": "Descricao do projeto",
            "resumo": "Resumo do projeto",
            "tipoProjeto": "TCC",
            "dataInicio": "2026-11-20",
            "dataFim": "2027-06-20",
            "professores": [
                {
                    "professorId": 10,
                    "papel": "ORIENTADOR"
                }
            ]
        }
        """;

        mockMvc.perform(post("/projetos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Acesso negado"));

        verify(professorClient).buscarPorId(10L, token);
    }
}
