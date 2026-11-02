package br.edu.ifrn.nc.portaltads.projects.infrastructure.storage;


import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ArmazenamentoArquivo {

    @Value("${app.storage.diretorio}")
    private String diretorio;

    private static final long TAMANHO_MAXIMO = 10 * 1024 * 1024;

    public String salvar(MultipartFile arquivo) throws IOException{

        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio.");
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("O arquivo não pode ultrapassar 10 MB.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();

        if (nomeOriginal == null || !nomeOriginal.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("O arquivo deve possuir extensão PDF.");
        }

        if (!"application/pdf".equals(arquivo.getContentType())) {
            throw new IllegalArgumentException("O arquivo deve ser do tipo PDF.");
        }

        byte[] assinatura;

        try (InputStream input = arquivo.getInputStream()) {
            assinatura = input.readNBytes(5);
        }

        if (!new String(assinatura, StandardCharsets.US_ASCII).equals("%PDF-")) {
            throw new IllegalArgumentException("O conteúdo do arquivo não é um PDF válido.");
        }

        Path caminhoDiretorio = Paths.get(diretorio);

        Files.createDirectories(caminhoDiretorio);

        String nomeArquivo = UUID.randomUUID() + ".pdf";

        Path caminhoArquivo = caminhoDiretorio.resolve(nomeArquivo);

        try (InputStream input = arquivo.getInputStream()) {
            Files.copy(input, caminhoArquivo);
        }

        return caminhoArquivo.toString();
    }

    private Path validarCaminho(String localizacao) {
        Path diretorioBase = Paths.get(diretorio)
                .toAbsolutePath()
                .normalize();

        Path caminhoArquivo = Paths.get(localizacao)
                .toAbsolutePath()
                .normalize();

        if (!caminhoArquivo.startsWith(diretorioBase)) {
            throw new IllegalArgumentException(
                    "O arquivo está fora do diretório permitido.");
        }

        return caminhoArquivo;
    }

    public void excluir(String localizacao) throws IOException {
        Path caminhoArquivo = validarCaminho(localizacao);
        Files.deleteIfExists(caminhoArquivo);
    }

    public Resource carregar(String localizacao) {
        Path caminhoArquivo = validarCaminho(localizacao);

        if (!Files.exists(caminhoArquivo)) {
            throw new ResourceNotFoundException("Arquivo não encontrado.");
        }

        return new FileSystemResource(caminhoArquivo);
    }
}
