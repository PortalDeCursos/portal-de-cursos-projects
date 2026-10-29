package br.edu.ifrn.nc.portaltads.projects.business.mapper;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.AutorProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoDocumentoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoProfessorResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    @Mapping(target = "autores", ignore = true)
    @Mapping(target = "disciplinas", ignore = true)
    @Mapping(target = "professores", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    Projetos toEntity(ProjetoRequestDTO dto);

    @Mapping(target = "autores", source = "autores")
    @Mapping(target = "disciplinasIds", source = "disciplinas")
    ProjetoResponseDTO toResponseDTO(Projetos entity);

    @Mapping(target = "statusProjeto", ignore = true)
    @Mapping(target = "autores", ignore = true)
    @Mapping(target = "disciplinas", ignore = true)
    @Mapping(target = "professores", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    void updateEntityFromDTO(ProjetoRequestDTO dto, @MappingTarget Projetos entity);

    List<AutorProjetoResponseDTO> mapAutores(List<AutorProjeto> autores);
    AutorProjetoResponseDTO toAutorResponseDTO(AutorProjeto autor);

    List<ProjetoProfessorResponseDTO> mapProfessores(
            List<ProjetoProfessor> professores
    );

    ProjetoProfessorResponseDTO toProjetoProfessorResponseDTO(
            ProjetoProfessor professor
    );

    List<ProjetoDocumentoResponseDTO> mapDocumentos(
            List<ProjetoDocumento> documentos
    );

    ProjetoDocumentoResponseDTO toProjetoDocumentoResponseDTO(
            ProjetoDocumento documento
    );

    default Long map(ProjetoDisciplina disciplina){
        return disciplina.getDisciplinaId();
    }
}
