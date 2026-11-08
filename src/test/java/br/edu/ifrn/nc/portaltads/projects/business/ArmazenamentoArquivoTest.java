package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.storage.ArmazenamentoArquivo;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArmazenamentoArquivoTest {

    @Test
    void deveLancarExcecaoQuandoEnviarArquivoDocx() {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                "uploads"
        );

        MultipartFile arquivo = mock(MultipartFile.class);

        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(1024L);
        when(arquivo.getOriginalFilename()).thenReturn("documento.docx");

        // WHEN
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> armazenamentoArquivo.salvar(arquivo)
        );

        // THEN
        assertEquals(
                "O arquivo deve possuir extensão PDF.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoArquivoUltrapassar10MB() {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        MultipartFile arquivo = mock(MultipartFile.class);

        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(10 * 1024 * 1024 + 1L);

        // WHEN
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> armazenamentoArquivo.salvar(arquivo)
        );

        // THEN
        assertEquals(
                "O arquivo não pode ultrapassar 10 MB.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoArquivoEstiverVazio() {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        MultipartFile arquivo = mock(MultipartFile.class);

        when(arquivo.isEmpty()).thenReturn(true);

        // WHEN
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> armazenamentoArquivo.salvar(arquivo)
        );

        // THEN
        assertEquals(
                "O arquivo não pode estar vazio.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoTipoDeConteudoNaoForPdf() {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        MultipartFile arquivo = mock(MultipartFile.class);

        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(1024L);
        when(arquivo.getOriginalFilename()).thenReturn("documento.pdf");
        when(arquivo.getContentType()).thenReturn("application/msword");

        // WHEN
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> armazenamentoArquivo.salvar(arquivo)
        );

        // THEN
        assertEquals(
                "O arquivo deve ser do tipo PDF.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoConteudoNaoForPdfValido() throws IOException{

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        MultipartFile arquivo = mock(MultipartFile.class);

        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(1024L);
        when(arquivo.getOriginalFilename()).thenReturn("documento.pdf");
        when(arquivo.getContentType()).thenReturn("application/pdf");

        byte[] conteudo = "Arquivo inválido".getBytes();

        when(arquivo.getInputStream()).thenReturn(
                new java.io.ByteArrayInputStream(conteudo)
        );

        // WHEN
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> armazenamentoArquivo.salvar(arquivo)
        );

        // THEN
        assertEquals(
                "O conteúdo do arquivo não é um PDF válido.",
                exception.getMessage()
        );
    }

    @Test
    void deveSalvarArquivoPdfComSucesso(@TempDir Path diretorioTemporario) throws IOException {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                diretorioTemporario.toString()
        );

        MultipartFile arquivo = mock(MultipartFile.class);

        byte[] conteudo = "%PDF-1.4 conteúdo do PDF".getBytes();

        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn((long) conteudo.length);
        when(arquivo.getOriginalFilename()).thenReturn("documento.pdf");
        when(arquivo.getContentType()).thenReturn("application/pdf");
        when(arquivo.getInputStream()).thenAnswer(
                invocation -> new java.io.ByteArrayInputStream(conteudo)
        );

        // WHEN
        String caminhoSalvo = armazenamentoArquivo.salvar(arquivo);

        // THEN
        assertNotNull(caminhoSalvo);
        assertTrue(Files.exists(Path.of(caminhoSalvo)));
        assertEquals(
                conteudo.length,
                Files.size(Path.of(caminhoSalvo))
        );
    }

    @Test
    void deveExcluirArquivoComSucesso(@TempDir Path diretorioTemporario)
            throws IOException {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                diretorioTemporario.toString()
        );

        Path arquivo = diretorioTemporario.resolve("documento.pdf");
        Files.write(arquivo, "%PDF-1.4 conteúdo".getBytes());

        // WHEN
        armazenamentoArquivo.excluir(arquivo.toString());

        // THEN
        assertFalse(Files.exists(arquivo));
    }

    @Test
    void deveCarregarArquivoComSucesso(@TempDir Path diretorioTemporario)
            throws IOException {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                diretorioTemporario.toString()
        );

        Path arquivo = diretorioTemporario.resolve("documento.pdf");
        Files.write(arquivo, "%PDF-1.4 conteúdo".getBytes());

        // WHEN
        Resource recurso = armazenamentoArquivo.carregar(arquivo.toString());

        // THEN
        assertNotNull(recurso);
        assertTrue(recurso.exists());
    }

    @Test
    void deveLancarExcecaoQuandoArquivoNaoExistir(@TempDir Path diretorioTemporario) {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                diretorioTemporario.toString()
        );

        String caminhoArquivo = diretorioTemporario
                .resolve("inexistente.pdf")
                .toString();

        // WHEN
        assertThrows(
                ResourceNotFoundException.class,
                () -> armazenamentoArquivo.carregar(caminhoArquivo)
        );
    }

    @Test
    void deveLancarExcecaoQuandoArquivoEstiverForaDoDiretorioPermitido(
            @TempDir Path diretorioTemporario) {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                diretorioTemporario.toString()
        );

        Path caminhoExterno = diretorioTemporario
                .getParent()
                .resolve("arquivo-externo.pdf");

        // WHEN
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> armazenamentoArquivo.carregar(caminhoExterno.toString())
        );

        // THEN
        assertEquals(
                "O arquivo está fora do diretório permitido.",
                exception.getMessage()
        );
    }

    @Test
    void deveExcluirArquivoInexistenteSemLancarExcecao(
            @TempDir Path diretorioTemporario) throws IOException {

        // GIVEN
        ArmazenamentoArquivo armazenamentoArquivo = new ArmazenamentoArquivo();

        ReflectionTestUtils.setField(
                armazenamentoArquivo,
                "diretorio",
                diretorioTemporario.toString()
        );

        String caminhoArquivo = diretorioTemporario
                .resolve("inexistente.pdf")
                .toString();

        // WHEN / THEN
        assertDoesNotThrow(
                () -> armazenamentoArquivo.excluir(caminhoArquivo)
        );
    }
}