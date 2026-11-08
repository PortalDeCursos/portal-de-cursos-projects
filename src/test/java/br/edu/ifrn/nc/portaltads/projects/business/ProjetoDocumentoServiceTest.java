package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoDownloadDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoDocumento;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoDocumentoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.storage.ArmazenamentoArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoDocumentoServiceTest {

    @Mock
    private ProjetoDocumentoRepository projetoDocumentoRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ArmazenamentoArquivo armazenamentoArquivo;

    @InjectMocks
    private ProjetoDocumentoService projetoDocumentoService;

    private Projetos projeto;

    @BeforeEach
    void setUp() {
        projeto = new Projetos();
        projeto.setId(1L);
        projeto.setNome("Projeto SmartNose");
    }

    @Test
    void deveEnviarDocumentoComSucesso() throws IOException {

        // GIVEN
        Long projetoId = 1L;

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "projeto.pdf",
                "application/pdf",
                "conteudo do PDF".getBytes()
        );

        ProjetoDocumento documento = new ProjetoDocumento();
        documento.setId(10L);
        documento.setProjeto(projeto);
        documento.setNomeArquivo("projeto.pdf");
        documento.setTipoDocumento("PDF");
        documento.setLocalizacao("projetos/projeto.pdf");

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(armazenamentoArquivo.salvar(arquivo))
                .thenReturn("projetos/projeto.pdf");

        when(projetoDocumentoRepository.save(any(ProjetoDocumento.class)))
                .thenReturn(documento);

        // WHEN
        ProjetoDocumentoResponseDTO resultado =
                projetoDocumentoService.enviarDocumento(projetoId, arquivo);

        // THEN
        assertEquals(10L, resultado.getId());
        assertEquals("projeto.pdf", resultado.getNomeArquivo());
        assertEquals("PDF", resultado.getTipoDocumento());
        assertEquals("projetos/projeto.pdf", resultado.getLocalizacao());

        verify(projetoRepository).findById(projetoId);
        verify(armazenamentoArquivo).salvar(arquivo);
        verify(projetoDocumentoRepository).save(any(ProjetoDocumento.class));

        verifyNoMoreInteractions(projetoRepository, projetoDocumentoRepository, armazenamentoArquivo);
    }

    @Test
    void deveLancarExcecaoQuandoEnviarDocumentoParaProjetoNaoEncontrado() throws IOException {

        // GIVEN
        Long projetoId = 999L;

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "projeto.pdf",
                "application/pdf",
                "conteudo do PDF".getBytes()
        );

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projetoDocumentoService.enviarDocumento(projetoId, arquivo)
        );

        assertEquals("Projeto não encontrado.", exception.getMessage());

        verify(projetoRepository).findById(projetoId);

        verifyNoInteractions(projetoDocumentoRepository, armazenamentoArquivo);
    }

    @Test
    void deveListarDocumentosComSucesso() {

        // GIVEN
        Long projetoId = 1L;

        ProjetoDocumento documento = new ProjetoDocumento();
        documento.setId(10L);
        documento.setProjeto(projeto);
        documento.setNomeArquivo("projeto.pdf");
        documento.setTipoDocumento("PDF");
        documento.setLocalizacao("projetos/projeto.pdf");

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(projetoDocumentoRepository.findByProjeto_id(projetoId))
                .thenReturn(List.of(documento));

        // WHEN
        List<ProjetoDocumentoResponseDTO> resultado =
                projetoDocumentoService.listarDocumentos(projetoId);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        assertEquals("projeto.pdf", resultado.get(0).getNomeArquivo());
        assertEquals("PDF", resultado.get(0).getTipoDocumento());
        assertEquals("projetos/projeto.pdf", resultado.get(0).getLocalizacao());

        verify(projetoRepository).findById(projetoId);
        verify(projetoDocumentoRepository).findByProjeto_id(projetoId);

        verifyNoMoreInteractions(projetoRepository, projetoDocumentoRepository);
        verifyNoInteractions(armazenamentoArquivo);
    }

    @Test
    void deveLancarExcecaoQuandoListarDocumentosDeProjetoNaoEncontrado() {

        // GIVEN
        Long projetoId = 999L;

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projetoDocumentoService.listarDocumentos(projetoId)
        );

        assertEquals("Projeto não encontrado.", exception.getMessage());

        verify(projetoRepository).findById(projetoId);

        verifyNoInteractions(projetoDocumentoRepository, armazenamentoArquivo);
    }

    @Test
    void deveBaixarDocumentoComSucesso() {

        // GIVEN
        Long projetoId = 1L;
        Long documentoId = 10L;

        ProjetoDocumento documento = new ProjetoDocumento();
        documento.setId(documentoId);
        documento.setProjeto(projeto);
        documento.setNomeArquivo("projeto.pdf");
        documento.setTipoDocumento("PDF");
        documento.setLocalizacao("projetos/projeto.pdf");

        Resource arquivo = mock(Resource.class);

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(projetoDocumentoRepository.findByIdAndProjeto_Id(
                documentoId, projetoId))
                .thenReturn(Optional.of(documento));

        when(armazenamentoArquivo.carregar("projetos/projeto.pdf"))
                .thenReturn(arquivo);

        // WHEN
        ProjetoDocumentoDownloadDTO resultado =
                projetoDocumentoService.baixarDocumento(projetoId, documentoId);

        // THEN
        assertEquals(arquivo, resultado.getArquivo());
        assertEquals("projeto.pdf", resultado.getNomeArquivo());

        verify(projetoRepository).findById(projetoId);
        verify(projetoDocumentoRepository)
                .findByIdAndProjeto_Id(documentoId, projetoId);
        verify(armazenamentoArquivo).carregar("projetos/projeto.pdf");

        verifyNoMoreInteractions(
                projetoRepository,
                projetoDocumentoRepository,
                armazenamentoArquivo
        );
    }

    @Test
    void deveLancarExcecaoQuandoBaixarDocumentoNaoEncontrado() {

        // GIVEN
        Long projetoId = 1L;
        Long documentoId = 999L;

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(projetoDocumentoRepository.findByIdAndProjeto_Id(
                documentoId, projetoId))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projetoDocumentoService.baixarDocumento(
                        projetoId, documentoId)
        );

        assertEquals("Documento não encontrado.", exception.getMessage());

        verify(projetoRepository).findById(projetoId);
        verify(projetoDocumentoRepository)
                .findByIdAndProjeto_Id(documentoId, projetoId);

        verifyNoInteractions(armazenamentoArquivo);
        verifyNoMoreInteractions(projetoRepository, projetoDocumentoRepository);
    }

    @Test
    void deveExcluirDocumentoComSucesso() throws IOException {

        // GIVEN
        Long projetoId = 1L;
        Long documentoId = 10L;

        ProjetoDocumento documento = new ProjetoDocumento();
        documento.setId(documentoId);
        documento.setProjeto(projeto);
        documento.setNomeArquivo("projeto.pdf");
        documento.setTipoDocumento("PDF");
        documento.setLocalizacao("projetos/projeto.pdf");

        TransactionSynchronizationManager.initSynchronization();

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(projetoDocumentoRepository.findByIdAndProjeto_Id(
                documentoId, projetoId))
                .thenReturn(Optional.of(documento));

        // WHEN
        projetoDocumentoService.excluirDocumento(projetoId, documentoId);

        // THEN
        verify(projetoRepository).findById(projetoId);

        verify(projetoDocumentoRepository)
                .findByIdAndProjeto_Id(documentoId, projetoId);

        verify(projetoDocumentoRepository).delete(documento);

        verifyNoInteractions(armazenamentoArquivo);

        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    void deveLancarExcecaoQuandoExcluirDocumentoNaoEncontrado() {

        // GIVEN
        Long projetoId = 1L;
        Long documentoId = 999L;

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(projetoDocumentoRepository.findByIdAndProjeto_Id(
                documentoId, projetoId))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projetoDocumentoService.excluirDocumento(
                        projetoId, documentoId)
        );

        assertEquals("Documento não encontrado.", exception.getMessage());

        verify(projetoRepository).findById(projetoId);

        verify(projetoDocumentoRepository)
                .findByIdAndProjeto_Id(documentoId, projetoId);

        verifyNoInteractions(armazenamentoArquivo);

        verify(projetoDocumentoRepository, never())
                .delete(any(ProjetoDocumento.class));
    }

    @Test
    void deveLancarExcecaoQuandoBaixarDocumentoDeProjetoNaoEncontrado() {

        // GIVEN
        Long projetoId = 999L;
        Long documentoId = 10L;

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projetoDocumentoService.baixarDocumento(
                        projetoId, documentoId)
        );

        assertEquals("Projeto não encontrado.", exception.getMessage());

        verify(projetoRepository).findById(projetoId);

        verifyNoInteractions(
                projetoDocumentoRepository,
                armazenamentoArquivo
        );
    }

    @Test
    void deveExcluirArquivoFisicoQuandoFalharAoSalvarDocumento() throws IOException {

        // GIVEN
        Long projetoId = 1L;
        String localizacao = "projetos/projeto.pdf";

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "projeto.pdf",
                "application/pdf",
                "conteudo do PDF".getBytes()
        );

        when(projetoRepository.findById(projetoId))
                .thenReturn(Optional.of(projeto));

        when(armazenamentoArquivo.salvar(arquivo))
                .thenReturn(localizacao);

        when(projetoDocumentoRepository.save(any(ProjetoDocumento.class)))
                .thenThrow(new RuntimeException("Falha ao salvar metadados"));

        // WHEN
        // THEN
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> projetoDocumentoService.enviarDocumento(projetoId, arquivo)
        );

        assertEquals("Falha ao salvar metadados", exception.getMessage());

        verify(projetoRepository).findById(projetoId);
        verify(armazenamentoArquivo).salvar(arquivo);

        verify(projetoDocumentoRepository).save(any(ProjetoDocumento.class));

        verify(armazenamentoArquivo).excluir(localizacao);

        verifyNoMoreInteractions(
                projetoRepository,
                projetoDocumentoRepository,
                armazenamentoArquivo
        );
    }
}