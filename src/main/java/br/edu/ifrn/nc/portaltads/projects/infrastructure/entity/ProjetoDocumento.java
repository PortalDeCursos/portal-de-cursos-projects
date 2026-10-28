package br.edu.ifrn.nc.portaltads.projects.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projeto_documento")
public class ProjetoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projetos projeto;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @Column(name = "tipo_documento", nullable = false, length = 50)
    private String tipoDocumento;

    @Column(name = "localizacao", nullable = false, length = 500)
    private String localizacao;

    @Column(name = "data_envio", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataEnvio;
}
