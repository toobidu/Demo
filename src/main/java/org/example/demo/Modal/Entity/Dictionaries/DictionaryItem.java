package org.example.demo.Modal.Entity.Dictionaries;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dictionary_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DictionaryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionaries;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;
}
