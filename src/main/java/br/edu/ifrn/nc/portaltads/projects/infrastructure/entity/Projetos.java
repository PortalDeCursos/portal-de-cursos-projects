package br.edu.ifrn.nc.portaltads.projects.infrastructure.entity;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projetos")
public class Projetos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "curso_id")
    private Long cursoId;

    @Column(name = "orientador_id")
    private Long orientadorId;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
    private List<AutorProjeto> autores = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
    private List<ProjetoDisciplina> disciplinas = new ArrayList<>();

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_projeto", nullable = false, length = 30)
    private TipoProjeto tipoProjeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_projeto", nullable = false, length = 30)
    private StatusProjeto statusProjeto;

    @Column(name = "arquivo_pdf")
    private String arquivoPdf;
}
