package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.StatusProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.mapper.ProjetoMapper;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.AutorProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoDisciplina;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ConflictException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        associarAutores(projeto, dto);
        associarDisciplinas(projeto, dto);

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
    public ProjetoResponseDTO listarPorId(Long id) {

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
        Projetos projeto = buscarProjetoExistente(id);
        projetoRepository.delete(projeto);
    }

    @Transactional
    public ProjetoResponseDTO atualizarStatus(Long id, StatusProjetoRequestDTO dto){

        Projetos projeto = buscarProjetoExistente(id);

        StatusProjeto statusAtual = projeto.getStatusProjeto();
        StatusProjeto novoStatus = dto.getStatusProjeto();

        boolean transicaoValida = switch (statusAtual){
            case EM_ANALISE ->
                    novoStatus == StatusProjeto.APROVADO || novoStatus == StatusProjeto.RECUSADO;
            case APROVADO ->
                    novoStatus == StatusProjeto.ANALISE_PUBLICACAO;
            case ANALISE_PUBLICACAO ->
                    novoStatus == StatusProjeto.PUBLICADO || novoStatus == StatusProjeto.RECUSADO_PUBLICACAO;

            case RECUSADO_PUBLICACAO ->
                    novoStatus == StatusProjeto.REVISAO;

            case REVISAO ->
                    novoStatus == StatusProjeto.EM_ANALISE;

            case RECUSADO, PUBLICADO -> false;
        };

        if (!transicaoValida){
            throw new IllegalArgumentException(
                    "Não é permitida a alteração do status "
            + statusAtual + " para " +novoStatus + ".");
        }

        projeto.setStatusProjeto(novoStatus);

        Projetos projetoAtualizado = projetoRepository.save(projeto);

        return projetoMapper.toResponseDTO(projetoAtualizado);

    }

    private Projetos buscarProjetoExistente(Long id){
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projeto não encontrado com o ID: " + id));
    }

    private void associarAutores(Projetos projeto, ProjetoRequestDTO dto){
        List<AutorProjeto> autores = dto.getAutores()
                .stream()
                .map(autorDTO -> {
                    AutorProjeto autor = new AutorProjeto();
                    autor.setNome(autorDTO.getNome());
                    autor.setEmail(autorDTO.getEmail());
                    autor.setProjeto(projeto);
                    return autor;
                })
                .toList();

        projeto.setAutores(new ArrayList<>(autores));
    }

    private void associarDisciplinas(Projetos projeto, ProjetoRequestDTO dto){
        List<ProjetoDisciplina> disciplinas = dto.getDisciplinaIds()
                .stream()
                .map(disciplinaId -> {
                    ProjetoDisciplina disciplina = new ProjetoDisciplina();
                    disciplina.setDisciplinaId(disciplinaId);
                    disciplina.setProjeto(projeto);
                    return disciplina;
                })
                .toList();

        projeto.setDisciplinas(new ArrayList<>(disciplinas));
    }

}
