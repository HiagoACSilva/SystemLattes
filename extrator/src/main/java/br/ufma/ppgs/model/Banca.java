package br.ufma.ppgs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "banca")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banca {
    @Id
    @Column(name = "id_banca")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nivel")
    private String nivel;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "ano")
    private String ano;

    @Column(name = "nome_candidato")
    private String discente;

    @Column(name = "nome_instituicao")
    private String instituicao;

    @Column(name = "nome_curso")
    private String curso;

    @ManyToOne
    @JoinColumn(name = "id_docente")
    Docente docente;
    
}
