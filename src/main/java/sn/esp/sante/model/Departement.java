package sn.esp.sante.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Table
@Entity
public class Departement {
	
	@Id
	@GeneratedValue()
	private Long id;
	@NotEmpty(message = "Le nom du service est requis")
	private String nom;
	@NotEmpty(message = "L'email est requis")
	@Column(name = "email", nullable = false, unique = true)
    @Email
    @Size(min = 5, max = 254, message = "L'email doit être compris entre 5 et 254 caractères")
	private String email;
	//private User medecin;
	@ManyToMany(mappedBy = "email")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	
    private Set<User> user = new HashSet<>();
	
	public Set<User> getUser() {
		return user;
	}
	public void setUser(Set<User> user) {
		this.user = user;
	}
	public Departement() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
