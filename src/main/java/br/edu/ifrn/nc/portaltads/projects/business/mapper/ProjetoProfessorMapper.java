package br.edu.ifrn.nc.portaltads.projects.business.mapper;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoProfessorRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoProfessorResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoProfessor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjetoProfessorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projeto", ignore = true)
    ProjetoProfessor toEntity(ProjetoProfessorRequestDTO dto);

    ProjetoProfessorResponseDTO toResponseDTO(ProjetoProfessor entity);
}
