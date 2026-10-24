package br.edu.ifrn.nc.portaltads.projects.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutorProjetoResponseDTO {

    private Long id;
    private String nome;
    private String email;
}
