package fr.eni.encheres.bo;

import java.time.LocalDateTime;
import java.util.Objects;

public class Enchere {

	private long idEnchere;
	private LocalDateTime dateEnchere;
	private int montant;
	private Utilisateur encherisseur;
	private ArticleAVendre article;
	
	public Enchere() {
	}

	public Enchere(long idEnchere, LocalDateTime dateEnchere, int montant, Utilisateur encherisseur,
			ArticleAVendre article) {
		this.idEnchere = idEnchere;
		this.dateEnchere = dateEnchere;
		this.montant = montant;
		this.encherisseur = encherisseur;
		this.article = article;
	}

	public long getIdEnchere() {
		return idEnchere;
	}

	public void setIdEnchere(long idEnchere) {
		this.idEnchere = idEnchere;
	}

	public LocalDateTime getDateEnchere() {
		return dateEnchere;
	}

	public void setDateEnchere(LocalDateTime dateEnchere) {
		this.dateEnchere = dateEnchere;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

	public Utilisateur getEncherisseur() {
		return encherisseur;
	}

	public void setEncherisseur(Utilisateur encherisseur) {
		this.encherisseur = encherisseur;
	}

	public ArticleAVendre getArticle() {
		return article;
	}

	public void setArticle(ArticleAVendre article) {
		this.article = article;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateEnchere, idEnchere, montant);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enchere other = (Enchere) obj;
		return Objects.equals(dateEnchere, other.dateEnchere) && idEnchere == other.idEnchere
				&& montant == other.montant;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enchere [idEnchere=");
		builder.append(idEnchere);
		builder.append(", dateEnchere=");
		builder.append(dateEnchere);
		builder.append(", montant=");
		builder.append(montant);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
