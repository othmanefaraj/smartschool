package edu.smartschool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Region.
 */
@Entity
@Table(name = "region")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "region")
public class Region implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "intitule", nullable = false)
    private String intitule;
    
    @ManyToOne
    @JoinColumn(name = "pays_id")
    private Pays pays;

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Ville> villes = new HashSet<>();

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Ecole> ecoles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntitule() {
        return intitule;
    }
    
    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public Pays getPays() {
        return pays;
    }

    public void setPays(Pays pays) {
        this.pays = pays;
    }

    public Set<Ville> getVilles() {
        return villes;
    }

    public void setVilles(Set<Ville> villes) {
        this.villes = villes;
    }

    public Set<Ecole> getEcoles() {
        return ecoles;
    }

    public void setEcoles(Set<Ecole> ecoles) {
        this.ecoles = ecoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Region region = (Region) o;
        if(region.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, region.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Region{" +
            "id=" + id +
            ", intitule='" + intitule + "'" +
            '}';
    }
}
