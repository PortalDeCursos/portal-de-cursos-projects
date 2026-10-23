package br.edu.ifrn.nc.portaltads.projects.business.dto.response;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoResponseDTO {

    private Long id;
    private Long cursoId;
    private Long orientadorId;
    private Long autorProjetoId;
    private String nome;
    private String descricao;
    private TipoProjeto tipoProjeto;
    private StatusProjeto statusProjeto;
    private String arquivoPdf;

}
