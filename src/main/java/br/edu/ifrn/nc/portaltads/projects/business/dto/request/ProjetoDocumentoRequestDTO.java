package br.edu.ifrn.nc.portaltads.projects.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoDocumentoRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String nomeArquivo;

    @NotBlank
    @Size(max = 50)
    private String tipoDocumento;

    @NotBlank
    @Size(max = 500)
    private String localizacao;
}
