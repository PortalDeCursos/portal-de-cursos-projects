package br.edu.ifrn.nc.portaltads.projects.infrastructure.entity;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.PapelProjetoProfessor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projeto_professor")
public class ProjetoProfessor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projetos projeto;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, length = 30)
    private PapelProjetoProfessor papel;
}
