package br.ufma.ppgs.repo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.ufma.ppgs.model.Discente;

public interface DiscenteRepository
        extends JpaRepository<Discente, Integer> {

    Optional<Discente> findById(Integer idDiscente);

    boolean existsById(Integer idDiscente);

    Discente findByNome(String nome);

    
}
