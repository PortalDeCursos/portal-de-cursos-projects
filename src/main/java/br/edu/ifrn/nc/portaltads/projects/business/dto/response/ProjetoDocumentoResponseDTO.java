package br.edu.ifrn.nc.portaltads.projects.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoDocumentoResponseDTO {

    private Long id;
    private String nomeArquivo;
    private String tipoDocumento;
    private String localizacao;
    private LocalDateTime dataEnvio;

}
