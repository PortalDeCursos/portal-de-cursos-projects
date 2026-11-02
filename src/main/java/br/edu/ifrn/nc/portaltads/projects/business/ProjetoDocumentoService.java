package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoDownloadDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoDocumento;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoDocumentoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.storage.ArmazenamentoArquivo;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoDocumentoService {

    private final ProjetoDocumentoRepository projetoDocumentoRepository;
    private final ProjetoRepository projetoRepository;
    private final ArmazenamentoArquivo armazenamentoArquivo;

    @Transactional
    public ProjetoDocumentoResponseDTO enviarDocumento(Long projetoId, MultipartFile arquivo) throws IOException{
        Projetos projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));

        String localizacao = armazenamentoArquivo.salvar(arquivo);

        ProjetoDocumento documento = new ProjetoDocumento();
        documento.setProjeto(projeto);
        documento.setNomeArquivo(arquivo.getOriginalFilename());
        documento.setTipoDocumento("PDF");
        documento.setLocalizacao(localizacao);

        ProjetoDocumento documentoSalvo;

        try {
            documentoSalvo = projetoDocumentoRepository.save(documento);
        } catch (RuntimeException e) {
            try {
                armazenamentoArquivo.excluir(localizacao);
            } catch (IOException erroExclusao) {
                e.addSuppressed(erroExclusao);
            }

            throw e;
        }

        ProjetoDocumentoResponseDTO response = new ProjetoDocumentoResponseDTO();

        response.setId(documentoSalvo.getId());
        response.setNomeArquivo(documentoSalvo.getNomeArquivo());
        response.setTipoDocumento(documentoSalvo.getTipoDocumento());
        response.setLocalizacao(documentoSalvo.getLocalizacao());
        response.setDataEnvio(documentoSalvo.getDataEnvio());

        return response;

    }

    @Transactional(readOnly = true)
    public List<ProjetoDocumentoResponseDTO> listarDocumentos(Long projetoId) {

        projetoRepository.findById(projetoId).orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));

        return projetoDocumentoRepository.findByProjeto_id(projetoId)
                .stream()
                .map(documento -> {
                    ProjetoDocumentoResponseDTO response = new ProjetoDocumentoResponseDTO();

                    response.setId(documento.getId());
                    response.setNomeArquivo(documento.getNomeArquivo());
                    response.setTipoDocumento(documento.getTipoDocumento());
                    response.setLocalizacao(documento.getLocalizacao());
                    response.setDataEnvio(documento.getDataEnvio());

                    return response;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjetoDocumentoDownloadDTO baixarDocumento(Long projetoId, Long documentoId){
        projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));

        ProjetoDocumento documento = projetoDocumentoRepository
                .findByIdAndProjeto_Id(documentoId, projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado."));

        Resource arquivo = armazenamentoArquivo.carregar(documento.getLocalizacao());

        ProjetoDocumentoDownloadDTO response = new ProjetoDocumentoDownloadDTO();
        response.setArquivo(arquivo);
        response.setNomeArquivo(documento.getNomeArquivo());

        return response;
    }

    @Transactional
    public void excluirDocumento(Long projetoId, Long documentoId) throws IOException{

        projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));

        ProjetoDocumento documento = projetoDocumentoRepository
                .findByIdAndProjeto_Id(documentoId, projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado."));

        armazenamentoArquivo.excluir(documento.getLocalizacao());
        projetoDocumentoRepository.delete(documento);
    }


}
