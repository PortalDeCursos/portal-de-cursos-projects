package br.edu.ifrn.nc.portaltads.projects.business.dto.response;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoResponseDTO {

    private Long id;
    private Long campusId;
    private Long cursoId;
    private List<AutorProjetoResponseDTO> autores;
    private List<Long> disciplinasIds;
    private String nome;
    private String resumo;
    private String descricao;
    private TipoProjeto tipoProjeto;
    private StatusProjeto statusProjeto;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<ProjetoProfessorResponseDTO> professores;
    private List<ProjetoDocumentoResponseDTO> documentos;

}
