package sn.esp.sante.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table
@Entity
public class Medecin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String specialite;
	@OneToOne
    @JoinColumn(unique = true)
    private User user;
	public Medecin() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Medecin(Long id, String specialite, User user) {
		super();
		this.id = id;
		this.specialite = specialite;
		this.user = user;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSpecialite() {
		return specialite;
	}
	public void setSpecialite(String specialite) {
		this.specialite = specialite;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "Medecin [id=" + id + ", specialite=" + specialite + ", user=" + user + "]";
	}
	
}
