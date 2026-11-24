package br.edu.ifrn.nc.portaltads.projects.business;

import br.edu.ifrn.nc.portaltads.projects.business.dto.DisciplinaDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.ProfessorDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.StatusProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.event.ProjetoStatusAlteradoEvent;
import br.edu.ifrn.nc.portaltads.projects.business.event.ProjetoSubmetidoEvent;
import br.edu.ifrn.nc.portaltads.projects.business.mapper.ProjetoMapper;
import br.edu.ifrn.nc.portaltads.projects.business.messaging.ProjetoEventPublisher;
import br.edu.ifrn.nc.portaltads.projects.client.*;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.*;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.PapelProjetoProfessor;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.AcessoNegadoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ConflictException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ResourceNotFoundException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.AutorProjetoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoProfessorRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.repository.ProjetoRepository;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.storage.ArmazenamentoArquivo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final AutorProjetoRepository autorProjetoRepository;
    private final ProjetoProfessorRepository projetoProfessorRepository;
    private final ProjetoMapper projetoMapper;
    private final ArmazenamentoArquivo armazenamentoArquivo;
    private final ProjetoEventPublisher projetoEventPublisher;
    private final CursoClient cursoClient;
    private final DisciplinaClient disciplinaClient;
    private final ProfessorClient professorClient;
    private final CampusClient campusClient;
    private final CoordenadorClient coordenadorClient;

    @Transactional
    public ProjetoResponseDTO cadastrar (ProjetoRequestDTO dto, String token) {

        campusClient.buscarPorID(dto.getCampusId());
        cursoClient.buscarPorID(dto.getCursoId());
        validarDisciplinas(dto);

        if (projetoRepository.existsByNome(dto.getNome())) {
            throw new ConflictException("Já existe um projeto cadastrado com esse nome.");
        }

        validarOrientador(dto);
        validarProfessores(dto, token);

        Projetos projeto = projetoMapper.toEntity(dto);
        projeto.setStatusProjeto(StatusProjeto.EM_ANALISE);

        associarAutores(projeto, dto);
        associarDisciplinas(projeto, dto);
        associarProfessores(projeto, dto);
        associarDocumentos(projeto, dto);


        Projetos projetoSalvo = projetoRepository.save(projeto);

        projetoEventPublisher.publicarProjetoSubmetido(montarEvento(projetoSalvo, dto, token));

        return projetoMapper.toResponseDTO(projetoSalvo);
    }

    private ProjetoSubmetidoEvent montarEvento(Projetos projeto, ProjetoRequestDTO dto, String token) {

        List<ProjetoSubmetidoEvent.AutorEvent> autores = dto.getAutores()
                .stream()
                .map(a -> new ProjetoSubmetidoEvent.AutorEvent(a.getNome(), a.getEmail()))
                .toList();

        List<ProjetoSubmetidoEvent.ProfessorEvent> professores = dto.getProfessores()
                .stream()
                .map(p -> new ProjetoSubmetidoEvent.ProfessorEvent(
                        p.getProfessorId(),
                        p.getPapel().name(),
                        professorClient.buscarPorId(p.getProfessorId(), token).usuario().email()))
                .toList();

        return new ProjetoSubmetidoEvent(
                projeto.getId(),
                dto.getNome(),
                dto.getTipoProjeto().name(),
                projeto.getStatusProjeto().name(),
                dto.getCursoId(),
                autores,
                professores,
                LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listar(){
        return projetoRepository.findAll()
                .stream()
                .map(projetoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO listarPorId(Long id) {

        Projetos projeto = buscarProjetoExistente(id);
        return projetoMapper.toResponseDTO(projeto);
    }


    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listarPorStatus(StatusProjeto statusProjeto){
        return projetoRepository.findByStatusProjeto(statusProjeto)
                .stream()
                .map(projetoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProjetoResponseDTO atualizar(Long id, ProjetoRequestDTO dto, String token) {
        Projetos projeto = buscarProjetoExistente(id);

        String[] dadosToken = extrairDadosToken(token);

        String email = dadosToken[0];
        String papel = dadosToken[1];

        verificarPermissaoAtualizacao(projeto, email, papel, token);

        campusClient.buscarPorID(dto.getCampusId());
        cursoClient.buscarPorID(dto.getCursoId());

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

        List<String> arquivosFisicos = projeto.getDocumentos()
                .stream()
                .map(ProjetoDocumento::getLocalizacao)
                .filter(localizacao -> localizacao != null)
                .filter(armazenamentoArquivo::pertenceAoDiretorio)
                .toList();

        projetoRepository.delete(projeto);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (String localizacao : arquivosFisicos) {
                            try {
                                armazenamentoArquivo.excluir(localizacao);
                            } catch (IOException e) {
                                throw new RuntimeException(
                                        "Não foi possível excluir o arquivo físico: "
                                                + localizacao,
                                        e);
                            }
                        }
                    }
                }
        );
    }


    @Transactional
    public ProjetoResponseDTO atualizarStatus(
            Long id, StatusProjetoRequestDTO dto, String token) {

        Projetos projeto = buscarProjetoExistente(id);

        String[] dadosToken = extrairDadosToken(token);

        String email = dadosToken[0];
        String papel = dadosToken[1];

        StatusProjeto statusAtual = projeto.getStatusProjeto();
        StatusProjeto novoStatus = dto.getStatusProjeto();

        verificarPermissaoAlteracaoStatus(
                projeto, email, papel, token, statusAtual, novoStatus);

        boolean transicaoValida = switch (statusAtual) {
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

        if (!transicaoValida) {
            throw new IllegalArgumentException(
                    "Não é permitida a alteração do status "
                            + statusAtual + " para " + novoStatus + ".");
        }

        projeto.setStatusProjeto(novoStatus);

        Projetos projetoAtualizado = projetoRepository.save(projeto);

        notificarMudancaStatus(projetoAtualizado, novoStatus, email, token);

        return projetoMapper.toResponseDTO(projetoAtualizado);
    }

    private void notificarMudancaStatus(Projetos projeto, StatusProjeto novoStatus,
                                        String emailLogado, String token) {
        try {
            List<ProfessorDTO> professores = projetoProfessorRepository
                    .findByProjeto_id(projeto.getId())
                    .stream()
                    .map(v -> professorClient.buscarPorId(v.getProfessorId(), token))
                    .toList();

            List<String> emailsAlunos = projeto.getAutores().stream()
                    .map(AutorProjeto::getEmail)
                    .toList();

            List<String> emailsProfessores = professores.stream()
                    .map(p -> p.usuario().email())
                    .toList();

            String nomeResponsavel = professores.stream()
                    .filter(p -> p.usuario().email().equals(emailLogado))
                    .map(p -> p.usuario().nome())
                    .findFirst()
                    .orElse("Professor orientador");

            List<String> destinatarios = switch (novoStatus) {
                case EM_ANALISE, PUBLICADO, RECUSADO_PUBLICACAO, REVISAO ->
                        juntar(emailsAlunos, emailsProfessores);
                case APROVADO, RECUSADO -> emailsAlunos;
                case ANALISE_PUBLICACAO -> List.of(buscarEmailCoordenador(projeto.getCursoId(), token));
            };

            projetoEventPublisher.publicarStatusAlterado(new ProjetoStatusAlteradoEvent(
                    projeto.getId(),
                    projeto.getNome(),
                    projeto.getTipoProjeto().name(),
                    novoStatus.name(),
                    nomeResponsavel,
                    destinatarios,
                    LocalDateTime.now()));

        } catch (Exception e) {
            log.error("Status do projeto {} foi alterado, mas a notificação falhou: {}",
                    projeto.getId(), e.getMessage());
        }
    }

    private String buscarEmailCoordenador(Long cursoId, String token) {

        var curso = cursoClient.buscarPorID(cursoId);

        if (curso.coordenadorId() == null) {
            throw new ResourceNotFoundException("Curso " + cursoId + " não possui coordenador designado.");
        }

        return coordenadorClient.buscarPorId(curso.coordenadorId(), token)
                .professor().usuario().email();
    }

    private List<String> juntar(List<String> a, List<String> b) {

        return Stream.concat(a.stream(), b.stream()).distinct().toList();
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

    private void associarProfessores(Projetos projeto, ProjetoRequestDTO dto) {
        List<ProjetoProfessor> professores = dto.getProfessores()
                .stream()
                .map(professorDTO -> {
                    ProjetoProfessor professor = new ProjetoProfessor();
                    professor.setProfessorId(professorDTO.getProfessorId());
                    professor.setPapel(professorDTO.getPapel());
                    professor.setProjeto(projeto);
                    return professor;
                })
                .toList();

        projeto.setProfessores(new ArrayList<>(professores));
    }

    private void validarOrientador(ProjetoRequestDTO dto) {

        long quantidadeOrientadores = dto.getProfessores()
                .stream()
                .filter(professor ->
                        professor.getPapel() == PapelProjetoProfessor.ORIENTADOR)
                .count();

        if (quantidadeOrientadores != 1) {
            throw new IllegalArgumentException(
                    "O projeto deve ter exatamente um professor orientador.");
        }
    }

    private void associarDocumentos(Projetos projeto, ProjetoRequestDTO dto) {

        if (dto.getDocumentos() == null || dto.getDocumentos().isEmpty()) {
            projeto.setDocumentos(new ArrayList<>());
            return;
        }

        List<ProjetoDocumento> documentos = dto.getDocumentos()
                .stream()
                .map(documentoDTO -> {
                    ProjetoDocumento documento = new ProjetoDocumento();
                    documento.setNomeArquivo(documentoDTO.getNomeArquivo());
                    documento.setTipoDocumento(documentoDTO.getTipoDocumento());
                    documento.setLocalizacao(documentoDTO.getLocalizacao());
                    documento.setProjeto(projeto);
                    return documento;
                })
                .toList();

        projeto.setDocumentos(new ArrayList<>(documentos));
    }


    private void validarDisciplinas(ProjetoRequestDTO dto) {

        List<DisciplinaDTO> disciplinas = disciplinaClient.buscarPorCurso(
                dto.getCursoId());

        for (Long disciplinaId : dto.getDisciplinaIds()) {

            boolean pertenceAoCurso = disciplinas.stream()
                    .anyMatch(disciplina ->
                            disciplina.id().equals(disciplinaId)
                                    && disciplina.cursoId().equals(dto.getCursoId()));

            if (!pertenceAoCurso) {
                throw new ResourceNotFoundException(
                        "Disciplina não encontrada no curso informado. ID: "
                                + disciplinaId);
            }
        }
    }

    private void validarProfessores(ProjetoRequestDTO dto, String token) {

        for (var professor : dto.getProfessores()) {
            professorClient.buscarPorId(professor.getProfessorId(), token);
        }
    }

    private void verificarPermissaoAtualizacao(
            Projetos projeto, String email, String papel, String token) {

        if ("ALUNO".equals(papel)) {

            boolean eAutor = autorProjetoRepository
                    .existsByProjetoIdAndEmail(projeto.getId(), email);

            if (!eAutor) {
                throw new AcessoNegadoException(
                        "Você não tem permissão para atualizar este projeto.");
            }

        } else if ("PROFESSOR".equals(papel)) {

            List<ProjetoProfessor> professores =
                    projetoProfessorRepository.findByProjeto_id(projeto.getId());

            boolean eOrientador = professores.stream()
                    .filter(p -> p.getPapel() == PapelProjetoProfessor.ORIENTADOR)
                    .anyMatch(p -> {
                        var professorDTO = professorClient.buscarPorId(
                                p.getProfessorId(), token);

                        return professorDTO.usuario().email().equals(email);
                    });

            if (!eOrientador) {
                throw new AcessoNegadoException(
                        "Você não tem permissão para atualizar este projeto.");
            }

        } else {
            throw new AcessoNegadoException(
                    "Seu papel não permite atualizar projetos.");
        }
    }

    private void verificarPermissaoAlteracaoStatus(
            Projetos projeto,
            String email,
            String papel,
            String token,
            StatusProjeto statusAtual,
            StatusProjeto novoStatus) {

        if ("ADMIN".equals(papel)) {
            return;
        }

        if ("PROFESSOR".equals(papel)) {

            boolean transicaoPermitida =
                    (statusAtual == StatusProjeto.EM_ANALISE
                            && (novoStatus == StatusProjeto.APROVADO
                            || novoStatus == StatusProjeto.RECUSADO))
                            ||
                            (statusAtual == StatusProjeto.APROVADO
                                    && novoStatus == StatusProjeto.ANALISE_PUBLICACAO)
                            ||
                            (statusAtual == StatusProjeto.REVISAO
                                    && novoStatus == StatusProjeto.EM_ANALISE);

            if (!transicaoPermitida) {
                throw new AcessoNegadoException(
                        "O professor orientador não tem permissão para realizar essa alteração de status.");
            }

            List<ProjetoProfessor> professores =
                    projetoProfessorRepository.findByProjeto_id(projeto.getId());

            boolean eOrientador = professores.stream()
                    .filter(p -> p.getPapel() == PapelProjetoProfessor.ORIENTADOR)
                    .anyMatch(p -> {
                        var professorDTO = professorClient.buscarPorId(
                                p.getProfessorId(), token);

                        return professorDTO.usuario().email().equals(email);
                    });

            if (!eOrientador) {
                throw new AcessoNegadoException(
                        "Somente o professor orientador pode alterar o status deste projeto.");
            }

            return;
        }

        if ("COORDENADOR".equals(papel)) {

            boolean transicaoPermitida =
                    (statusAtual == StatusProjeto.ANALISE_PUBLICACAO
                            && (novoStatus == StatusProjeto.PUBLICADO
                            || novoStatus == StatusProjeto.RECUSADO_PUBLICACAO))
                            ||
                            (statusAtual == StatusProjeto.RECUSADO_PUBLICACAO
                                    && novoStatus == StatusProjeto.REVISAO);

            if (!transicaoPermitida) {
                throw new AcessoNegadoException(
                        "O coordenador não tem permissão para realizar essa alteração de status.");
            }

            var curso = cursoClient.buscarPorID(projeto.getCursoId());

            if (curso.coordenadorId() == null) {
                throw new ResourceNotFoundException(
                        "O curso não possui coordenador designado.");
            }

            var coordenador = coordenadorClient.buscarPorId(
                    curso.coordenadorId(), token);

            boolean eCoordenador = coordenador.professor()
                    .usuario()
                    .email()
                    .equals(email);

            if (!eCoordenador) {
                throw new AcessoNegadoException(
                        "Somente o coordenador responsável pelo curso pode alterar o status deste projeto.");
            }

            return;
        }

        throw new AcessoNegadoException(
                "Seu papel não permite alterar o status de projetos.");
    }

    private String[] extrairDadosToken(String token) {
        try {
            String tokenLimpo = token.replace("Bearer ", "");
            String[] partes = tokenLimpo.split("\\.");

            byte[] payload = Base64.getUrlDecoder().decode(partes[1]);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode dados = mapper.readTree(payload);

            String email = dados.get("sub").asText();
            String papel = dados.get("role").asText();

            return new String[]{email, papel};

        } catch (Exception e) {
            throw new AcessoNegadoException("Token inválido.");
        }
    }

}
