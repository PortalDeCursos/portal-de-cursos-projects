
package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.AutorProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoProfessorRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.mapper.ProjetoMapper;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.PapelProjetoProfessor;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ConflictException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.storage.ArmazenamentoArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ProjetoMapper projetoMapper;

    @Mock
    private ArmazenamentoArquivo armazenamentoArquivo;

    @InjectMocks
    private ProjetoService projetoService;

    private Projetos projeto;
    private ProjetoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        projeto = new Projetos();
        projeto.setId(1L);
        projeto.setNome("Projeto SmartNose");

        responseDTO = new ProjetoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Projeto SmartNose");
    }

    @Test
    void deveListarProjetosComSucesso() {

        // GIVEN
        when(projetoRepository.findAll()).thenReturn(List.of(projeto));
        when(projetoMapper.toResponseDTO(projeto)).thenReturn(responseDTO);

        // WHEN
        List<ProjetoResponseDTO> resultado = projetoService.listar();

        // THEN
        assertThat(resultado)
                .hasSize(1)
                .containsExactly(responseDTO);

        assertEquals(1L, resultado.get(0).getId());
        assertEquals("Projeto SmartNose", resultado.get(0).getNome());

        verify(projetoRepository).findAll();
        verify(projetoMapper).toResponseDTO(projeto);

        verifyNoMoreInteractions(projetoRepository, projetoMapper);
    }

    @Test
    void deveConsultarProjetoPorIdComSucesso() {

        // GIVEN
        Long projetoId = 1L;

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(projetoMapper.toResponseDTO(projeto))
                .thenReturn(responseDTO);

        // WHEN
        ProjetoResponseDTO resultado = projetoService.listarPorId(projetoId);

        // THEN
        assertThat(resultado)
                .isNotNull()
                .isEqualTo(responseDTO);

        assertEquals(projetoId, resultado.getId());
        assertEquals("Projeto SmartNose", resultado.getNome());

        verify(projetoRepository).findById(projetoId);
        verify(projetoMapper).toResponseDTO(projeto);

        verifyNoMoreInteractions(projetoRepository, projetoMapper);
    }

    @Test
    void deveLancarExcecaoQuandoProjetoNaoEncontrado() {

        // GIVEN
        Long projetoId = 999L;

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThrows(
                ResourceNotFoundException.class,
                () -> projetoService.listarPorId(projetoId)
        );

        verify(projetoRepository).findById(projetoId);

        verifyNoInteractions(projetoMapper, armazenamentoArquivo);
    }


    @Test
    void deveCadastrarProjetoComSucesso() {

        // GIVEN
        ProjetoRequestDTO dto = new ProjetoRequestDTO();

        dto.setCursoId(1L);
        dto.setCampusId(1L);

        AutorProjetoRequestDTO autorDTO = new AutorProjetoRequestDTO();
        autorDTO.setNome("Luana Gomes");
        autorDTO.setEmail("luana@email.com");

        dto.setAutores(List.of(autorDTO));
        dto.setDisciplinaIds(List.of(1L));

        dto.setNome("Projeto SmartNose");
        dto.setDescricao("Projeto de desenvolvimento de um sistema.");
        dto.setResumo("Sistema para identificação de odores.");
        dto.setTipoProjeto(TipoProjeto.TCC);
        dto.setDataInicio(LocalDate.of(2026, 1, 1));
        dto.setDataFim(LocalDate.of(2026, 12, 31));

        ProjetoProfessorRequestDTO professorDTO =
                new ProjetoProfessorRequestDTO(
                        1L,
                        PapelProjetoProfessor.ORIENTADOR
                );

        dto.setProfessores(List.of(professorDTO));
        dto.setDocumentos(null);

        when(projetoRepository.existsByNome(dto.getNome()))
                .thenReturn(false);

        when(projetoMapper.toEntity(dto))
                .thenReturn(projeto);

        when(projetoRepository.save(projeto))
                .thenReturn(projeto);

        when(projetoMapper.toResponseDTO(projeto))
                .thenReturn(responseDTO);

        // WHEN
        ProjetoResponseDTO resultado = projetoService.cadastrar(dto);

        // THEN
        assertThat(resultado)
                .isNotNull()
                .isEqualTo(responseDTO);

        assertEquals(1L, resultado.getId());
        assertEquals("Projeto SmartNose", resultado.getNome());

        assertEquals(StatusProjeto.EM_ANALISE, projeto.getStatusProjeto());

        assertThat(projeto.getAutores()).hasSize(1);
        assertThat(projeto.getDisciplinas()).hasSize(1);
        assertThat(projeto.getProfessores()).hasSize(1);
        assertThat(projeto.getDocumentos()).isEmpty();

        verify(projetoRepository).existsByNome(dto.getNome());
        verify(projetoMapper).toEntity(dto);
        verify(projetoRepository).save(projeto);
        verify(projetoMapper).toResponseDTO(projeto);

        verifyNoMoreInteractions(projetoRepository, projetoMapper);
        verifyNoInteractions(armazenamentoArquivo);
    }

    @Test
    void deveLancarExcecaoQuandoCadastrarProjetoComNomeDuplicado() {
        // GIVEN
        ProjetoRequestDTO dto = new ProjetoRequestDTO();
        dto.setNome("Projeto SmartNose");

        when(projetoRepository.existsByNome(dto.getNome()))
                .thenReturn(true);

        // WHEN
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> projetoService.cadastrar(dto)
        );

        // THEN
        assertEquals(
                "Já existe um projeto cadastrado com esse nome.",
                exception.getMessage()
        );

        verify(projetoRepository).existsByNome(dto.getNome());

        verifyNoMoreInteractions(projetoRepository);
        verifyNoInteractions(projetoMapper, armazenamentoArquivo);
    }

}
