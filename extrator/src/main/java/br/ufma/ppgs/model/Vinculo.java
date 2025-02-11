package br.ufma.ppgs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vinculo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vinculo {
    @Id
    @Column(name = "id_vinculo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name="tipo")
    String tipo;

    @Column(name = "nome_instituicao")
    String nomeInstituicao;

    @Column(name = "ano_inicio")
    String anoInicio;
    
    @Column(name = "ano_fim")
    String anoFim;

    @Column(name = "outras_informacoes")
    String outrasInformacoes;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "id_docente")
    Docente docente;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "id_discente")
    Discente discente;
}
