package edu.smartschool.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import edu.smartschool.domain.enumeration.Categorie;

/**
 * A Ecole.
 */
@Entity
@Table(name = "ecole")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "ecole")
public class Ecole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "intitule", nullable = false)
    private String intitule;
    
    @Column(name = "adresse")
    private String adresse;
    
    @Column(name = "site_web")
    private String siteWeb;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie")
    private Categorie categorie;
    
    @Column(name = "gps_latitude")
    private String gpsLatitude;
    
    @Column(name = "gps_longitude")
    private String gpsLongitude;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "pays_id")
    private Pays pays;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne
    @JoinColumn(name = "ville_id")
    private Ville ville;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "ecole_classe_type",
               joinColumns = @JoinColumn(name="ecoles_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="classe_types_id", referencedColumnName="ID"))
    private Set<ClasseType> classeTypes = new HashSet<>();

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

    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getSiteWeb() {
        return siteWeb;
    }
    
    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public Categorie getCategorie() {
        return categorie;
    }
    
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }
    
    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }
    
    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pays getPays() {
        return pays;
    }

    public void setPays(Pays pays) {
        this.pays = pays;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public Set<ClasseType> getClasseTypes() {
        return classeTypes;
    }

    public void setClasseTypes(Set<ClasseType> classeTypes) {
        this.classeTypes = classeTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ecole ecole = (Ecole) o;
        if(ecole.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, ecole.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ecole{" +
            "id=" + id +
            ", intitule='" + intitule + "'" +
            ", adresse='" + adresse + "'" +
            ", siteWeb='" + siteWeb + "'" +
            ", categorie='" + categorie + "'" +
            ", gpsLatitude='" + gpsLatitude + "'" +
            ", gpsLongitude='" + gpsLongitude + "'" +
            '}';
    }
}
