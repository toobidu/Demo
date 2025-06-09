package org.example.demo.Modal.Entity.Dictionaries;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dictionaries")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "dictionaries", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DictionaryItem> dictionaryItems = new HashSet<>();

}
