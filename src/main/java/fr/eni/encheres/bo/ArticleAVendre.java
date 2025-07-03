package fr.eni.encheres.bo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ArticleAVendre implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long idArticle;
	@NotNull
	private Categorie categorie = new Categorie();
	@NotBlank
	@Size(min = 10, max = 30)
	private String nomArticle;
	@NotBlank
	@Size(min = 20, max = 300)
	private String description;
	@NotNull
	private LocalDateTime dateDebutEncheres;
	@NotNull
	private LocalDateTime dateFinEncheres;
	private int status;
	@NotNull
	@Min(value = 1)
	private int miseAPrix;
	private int prixVente;
	private EtatVente etatVente;
	
	private Utilisateur vendeur;
	@NotNull
	private Adresse adresseRetrait;
	private String photo;
	
	
	public ArticleAVendre() {
		super();
	}

	public ArticleAVendre(long idArticle, @NotNull Categorie categorie,
			@NotBlank @Size(min = 10, max = 30) String nomArticle,
			@NotBlank @Size(min = 20, max = 300) String description, @NotNull @NotNull LocalDateTime dateDebutEncheres,
			@NotNull @NotNull LocalDateTime dateFinEncheres, int status, @NotNull @Min(1) int miseAPrix, int prixVente,
			EtatVente etatVente, Utilisateur vendeur, @NotNull Adresse adresseRetrait, String photo) {
		super();
		this.idArticle = idArticle;
		this.categorie = categorie;
		this.nomArticle = nomArticle;
		this.description = description;
		this.dateDebutEncheres = dateDebutEncheres;
		this.dateFinEncheres = dateFinEncheres;
		this.status = status;
		this.miseAPrix = miseAPrix;
		this.prixVente = prixVente;
		this.etatVente = etatVente;
		this.vendeur = vendeur;
		this.adresseRetrait = adresseRetrait;
		this.photo = photo;
	}

	
	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}


	public long getIdArticle() {
		return idArticle;
	}

	public void setIdArticle(long idArticle) {
		this.idArticle = idArticle;
	}


	public String getNomArticle() {
		return nomArticle;
	}

	public void setNomArticle(String nomArticle) {
		this.nomArticle = nomArticle;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
 
	
	public LocalDateTime getDateDebutEncheres() {
		return dateDebutEncheres;
	}
 
	public void setDateDebutEncheres(LocalDateTime dateDebutEncheres) {
		this.dateDebutEncheres = dateDebutEncheres;
	}

	public LocalDateTime getDateFinEncheres() {
		return dateFinEncheres;
	}

	public void setDateFinEncheres(LocalDateTime dateFinEncheres) {
		this.dateFinEncheres = dateFinEncheres;
	}

	public int getMiseAPrix() {
		return miseAPrix;
	}

	public void setMiseAPrix(int miseAPrix) {
		this.miseAPrix = miseAPrix;
	}

	public int getPrixVente() {
		return prixVente;
	}

	public void setPrixVente(int prixVente) {
		this.prixVente = prixVente;
	}

	public EtatVente getEtatVente() {
		return etatVente;
	}

	public void setEtatVente(EtatVente etatVente) {
		this.etatVente = etatVente;
	}


	public Utilisateur getVendeur() {
		return vendeur;
	}


	public void setVendeur(Utilisateur vendeur) {
		this.vendeur = vendeur;
	}

	public Adresse getAdresseRetrait() {
		return adresseRetrait;
	}


	public void setAdresseRetrait(Adresse adresseRetrait) {
		this.adresseRetrait = adresseRetrait;
	}





	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArticleAVendre [idArticle=");
		builder.append(idArticle);
		builder.append(", categorie=");
		builder.append(categorie);
		builder.append(", nomArticle=");
		builder.append(nomArticle);
		builder.append(", description=");
		builder.append(description);
		builder.append(", dateDebutEncheres=");
		builder.append(dateDebutEncheres);
		builder.append(", dateFinEncheres=");
		builder.append(dateFinEncheres);
		builder.append(", status=");
		builder.append(status);
		builder.append(", miseAPrix=");
		builder.append(miseAPrix);
		builder.append(", prixVente=");
		builder.append(prixVente);
		builder.append(", etatVente=");
		builder.append(etatVente);
		builder.append(", vendeur=");
		builder.append(vendeur);
		builder.append(", adresseRetrait=");
		builder.append(adresseRetrait);
		builder.append(", photo=");
		builder.append(photo);
		builder.append("]");
		return builder.toString();
	}


	@Override
	public int hashCode() {
		return Objects.hash(adresseRetrait, categorie, dateDebutEncheres, dateFinEncheres, description, etatVente,
				idArticle, miseAPrix, nomArticle, photo, prixVente, status, vendeur);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleAVendre other = (ArticleAVendre) obj;
		return Objects.equals(adresseRetrait, other.adresseRetrait) && Objects.equals(categorie, other.categorie)
				&& Objects.equals(dateDebutEncheres, other.dateDebutEncheres)
				&& Objects.equals(dateFinEncheres, other.dateFinEncheres)
				&& Objects.equals(description, other.description) && etatVente == other.etatVente
				&& idArticle == other.idArticle && miseAPrix == other.miseAPrix
				&& Objects.equals(nomArticle, other.nomArticle) && Objects.equals(photo, other.photo)
				&& prixVente == other.prixVente && status == other.status && Objects.equals(vendeur, other.vendeur);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
