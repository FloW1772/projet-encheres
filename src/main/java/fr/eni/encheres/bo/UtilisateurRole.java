package fr.eni.encheres.bo;

import java.util.Objects;

public class UtilisateurRole {
	
	private long idUtilisateur;
    private int idRole;
    
    public UtilisateurRole() {
	
	}
    
	public UtilisateurRole(long idUtilisateur, int idRole) {
		this.idUtilisateur = idUtilisateur;
		this.idRole = idRole;
	}

	public long getIdUtilisateur() {
		return idUtilisateur;
	}

	public void setIdUtilisateur(long idUtilisateur) {
		this.idUtilisateur = idUtilisateur;
	}

	public int getIdRole() {
		return idRole;
	}

	public void setIdRole(int idRole) {
		this.idRole = idRole;
	}

	@Override
	public String toString() {
		return String.format("UtilisateurRole [idUtilisateur=%s, idRole=%s]", idUtilisateur, idRole);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idRole, idUtilisateur);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UtilisateurRole other = (UtilisateurRole) obj;
		return idRole == other.idRole && idUtilisateur == other.idUtilisateur;
	}
    
    
    
    

}
