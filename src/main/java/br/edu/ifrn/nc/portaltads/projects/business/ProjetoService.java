package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.mapper.ProjetoMapper;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ConflictException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ProjetoMapper projetoMapper;

    @Transactional
    public ProjetoResponseDTO cadastrar (ProjetoRequestDTO dto) {

        if (projetoRepository.existsByNome(dto.getNome())) {
            throw new ConflictException("Já existe um projeto cadastrado com esse nome.");
        }
        Projetos projeto = projetoMapper.toEntity(dto);
        projeto.setStatusProjeto(StatusProjeto.EM_ANALISE);
        Projetos projetoSalvo = projetoRepository.save(projeto);

        return projetoMapper.toResponseDTO(projetoSalvo);
    }

    @Transactional
    public List<ProjetoResponseDTO> listar(){
        return projetoRepository.findAll()
                .stream()
                .map(projetoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProjetoResponseDTO listarPorID (Long id) {

        Projetos projeto = buscarProjetoExistente(id);
        return projetoMapper.toResponseDTO(projeto);
    }

    @Transactional
    public ProjetoResponseDTO atualizar(Long id, ProjetoRequestDTO dto){
        Projetos projeto = buscarProjetoExistente(id);

        if (projetoRepository.existsByNomeAndIdNot(dto.getNome(), id)) {
            throw new ConflictException("Já existe outro projeto cadastrado com esse nome.");
        }

        projetoMapper.updateEntityFromDTO(dto, projeto);
        Projetos projetoAtualizado = projetoRepository.save(projeto);

        return projetoMapper.toResponseDTO(projetoAtualizado);
    }

    @Transactional
    public void excluir(Long id) {
        Projetos projeto= buscarProjetoExistente(id);
        projetoRepository.delete(projeto);
    }

    private Projetos buscarProjetoExistente(Long id){
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projeto não encontrado com o ID: " + id));
    }


}
