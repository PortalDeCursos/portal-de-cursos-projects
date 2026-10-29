package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoProfessorRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoProfessorResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.mapper.ProjetoProfessorMapper;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoProfessor;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoProfessorRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoProfessorService {

    private final ProjetoProfessorRepository projetoProfessorRepository;
    private final ProjetoRepository projetoRepository;
    private final ProjetoProfessorMapper projetoProfessorMapper;

    @Transactional
    public ProjetoProfessorResponseDTO associarProfessor(Long projetoId, ProjetoProfessorRequestDTO dto) {

        Projetos projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Projeto não encontrado com o ID: " + projetoId));

        ProjetoProfessor vinculo = projetoProfessorMapper.toEntity(dto);
        vinculo.setProjeto(projeto);

        ProjetoProfessor salvo = projetoProfessorRepository.save(vinculo);

        return projetoProfessorMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProjetoProfessorResponseDTO> listarPorProjeto(Long projetoId) {

        if (!projetoRepository.existsById(projetoId)) {
            throw new ResourceNotFoundException("Projeto não encontrado com o ID: " + projetoId);
        }

        return projetoProfessorRepository.findByProjeto_id(projetoId)
                .stream()
                .map(projetoProfessorMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void removerVinculo(Long projetoId, Long vinculoId) {

        ProjetoProfessor vinculo = projetoProfessorRepository.findById(vinculoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo de professor não encontrado com o ID: " + vinculoId));

        if (!vinculo.getProjeto().getId().equals(projetoId)) {
            throw new ResourceNotFoundException(
                    "Vínculo de professor não encontrado no projeto informado.");
        }

        projetoProfessorRepository.delete(vinculo);
    }
}
