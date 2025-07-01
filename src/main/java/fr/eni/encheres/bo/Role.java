package fr.eni.encheres.bo;

import java.util.Objects;

public class Role {
	
	 private long idRole;
	 private String libelle;
	 
	public Role() {
	}

	public Role(long idRole, String libelle) {
		super();
		this.idRole = idRole;
		this.libelle = libelle;
	}


	public long getIdRole() {
		return idRole;
	}

	public void setIdRole(int idRole) {
		this.idRole = idRole;
	}


	public String getLibelle() {
		return libelle;
	}


	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}


	@Override
	public String toString() {
		return String.format("Role [idRole=%s, libelle=%s]", idRole, libelle);
	}


	@Override
	public int hashCode() {
		return Objects.hash(idRole, libelle);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		return idRole == other.idRole && Objects.equals(libelle, other.libelle);
	}
	 

}
