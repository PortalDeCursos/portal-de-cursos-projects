package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoProfessorRepository extends JpaRepository<ProjetoProfessor, Long> {
}
