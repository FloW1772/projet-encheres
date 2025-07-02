package fr.eni.encheres.bo;

import java.util.Objects;

public class Categorie {

	
	
	
	private int idCategorie;
	
	private String libelle;
	

	public Categorie() {
	}

	public Categorie(String libelle) {
		this.libelle = libelle;
	}

	public Categorie(int idCategorie, String libelle) {
		this.idCategorie = idCategorie;
		this.libelle = libelle;
	}

	public int getIdCategorie() {
		return idCategorie;
	}

	public void setIdCategorie(int idCategorie) {
		this.idCategorie = idCategorie;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idCategorie);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Categorie other = (Categorie) obj;
		return idCategorie == other.idCategorie;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Categorie [idCategorie=");
		builder.append(idCategorie);
		builder.append(", libelle=");
		builder.append(libelle);
		builder.append("]");
		return builder.toString();
	}

	
}
