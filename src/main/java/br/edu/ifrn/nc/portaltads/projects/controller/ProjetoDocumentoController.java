package br.edu.ifrn.nc.portaltads.projects.controller;

import br.edu.ifrn.nc.portaltads.projects.business.ProjetoDocumentoService;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoDownloadDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/projetos/{projetoId}/documentos")
@RequiredArgsConstructor
public class ProjetoDocumentoController {

    private final ProjetoDocumentoService projetoDocumentoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjetoDocumentoResponseDTO> enviarDocumento(
            @PathVariable Long projetoId, @RequestParam("arquivo")MultipartFile arquivo) throws IOException{

        ProjetoDocumentoResponseDTO response = projetoDocumentoService.enviarDocumento(projetoId, arquivo);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjetoDocumentoResponseDTO>> listarDocumentos(@PathVariable Long projetoId){

        List<ProjetoDocumentoResponseDTO> documentos = projetoDocumentoService.listarDocumentos(projetoId);

        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/{documentoId}/download")
    public ResponseEntity<Resource> baixarDocumento(
            @PathVariable Long projetoId, @PathVariable Long documentoId) throws IOException{

        ProjetoDocumentoDownloadDTO documento = projetoDocumentoService.baixarDocumento(projetoId, documentoId);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + documento.getNomeArquivo() + "\"")
                .body(documento.getArquivo());
    }
}
