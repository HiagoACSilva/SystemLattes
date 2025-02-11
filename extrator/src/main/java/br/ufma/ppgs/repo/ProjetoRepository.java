package br.ufma.ppgs.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ufma.ppgs.model.Docente;
import br.ufma.ppgs.model.Discente;
import br.ufma.ppgs.model.Projeto;

public interface ProjetoRepository 
    extends JpaRepository<Projeto, Integer>{

    Projeto findByTituloAndAnoInicioAndDocente(String titulo, String anoInicio, Docente docente);
    Projeto findByTituloAndAnoInicioAndDiscente(String titulo, String anoInicio, Discente discente);
    
}
