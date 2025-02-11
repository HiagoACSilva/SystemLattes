package br.ufma.ppgs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "premio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Premio {
    @Id
    @Column(name = "id_premio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_premio")
    private String nome;

    @Column(name = "entidade")
    private String entidade;

    @Column(name = "ano")
    private String ano;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "id_docente")
    Docente docente;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "id_discente")
    Discente discente;
}
