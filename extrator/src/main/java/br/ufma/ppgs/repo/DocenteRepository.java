package br.ufma.ppgs.repo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.ufma.ppgs.model.Docente;

public interface DocenteRepository
        extends JpaRepository<Docente, Integer> {

    Optional<Docente> findById(Integer idDocente);

    boolean existsById(Integer idDocente);

    Docente findByNome(String nome);

    
}
