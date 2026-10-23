package br.edu.ifrn.nc.portaltads.projects.business.mapper;

import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    Projetos toEntity(ProjetoRequestDTO dto);
    ProjetoResponseDTO toResponseDTO(Projetos entity);
    void updateEntityFromDTO(ProjetoRequestDTO dto, @MappingTarget Projetos entity);
}
