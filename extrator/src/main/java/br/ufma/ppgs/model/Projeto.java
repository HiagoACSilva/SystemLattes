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
@Table(name = "projeto")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto")
    Integer id;

    @Column(name = "titulo")
    String titulo; 

    @Column(name = "ano_inicio")
    String anoInicio;
    
    @Column(name = "ano_fim")
    String anoFim;

    @Column(name = "situacao")
    String situacao;
    
    @Column(name = "natureza")
    String natureza;
    
    @Column(name = "qtd_graduacao")
    String qtdGraduacao;
    
    @Column(name = "qtd_especializacao")
    String qtdEspec;

    @Column(name = "qtd_mestrado")
    String qtdMestrado;

    @Column(name = "qtd_doutorado")
    String qtdDoutorado;

    @Column(name = "descricao")
    String descricao;

    @Column(name = "integrantes")
    String integrantes;

    @Column(name = "financiador")
    String financiador;

    @Column(name = "responsavel")
    String responsavel;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "id_docente")
    Docente docente;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "id_discente")
    Discente discente;
}
