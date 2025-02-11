package br.ufma.ppgs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "programa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programa")
    Integer id;

    @Column(name = "nome")
    String nome;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "programa_docente", joinColumns = @JoinColumn(name = "id_programa"), inverseJoinColumns = @JoinColumn(name = "id_docente"))
    List<Docente> docentes;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "programa_discente", joinColumns = @JoinColumn(name = "id_programa"), inverseJoinColumns = @JoinColumn(name = "id_discente"))
    List<Discente> discentes;

}
