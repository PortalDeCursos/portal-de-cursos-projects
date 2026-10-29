package br.edu.ifrn.nc.portaltads.projects.infrastructure.entity;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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

    @Column(name = "campus_id", nullable = false)
    private Long campusId;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
    private List<AutorProjeto> autores = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
    private List<ProjetoDisciplina> disciplinas = new ArrayList<>();

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "resumo", nullable = false, length = 500)
    private String resumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_projeto", nullable = false, length = 30)
    private TipoProjeto tipoProjeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_projeto", nullable = false, length = 30)
    private StatusProjeto statusProjeto;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
    private List<ProjetoProfessor> professores = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
    private List<ProjetoDocumento> documentos = new ArrayList<>();
}
