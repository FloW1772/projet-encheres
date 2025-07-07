package fr.eni.encheres.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Utilisateur {

    private long idUtilisateur;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Le pseudo doit être alphanumérique")
    private String pseudo;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    @Email(message = "Email invalide")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String telephone;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    private int credit = 100;

    @NotNull
    private List<Role> roles = new ArrayList<>();

    private Adresse adresse = new Adresse();

    private List<ArticleAVendre> articles = new ArrayList<>();

    private List<Enchere> encheres = new ArrayList<>();

    public Utilisateur() {
    }

    public Utilisateur(long idUtilisateur, String pseudo, String nom, String prenom, String email, String telephone,
                       String motDePasse, int credit, List<Role> roles, Adresse adresse,
                       List<ArticleAVendre> articles, List<Enchere> encheres) {
        this.idUtilisateur = idUtilisateur;
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.credit = credit;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.adresse = adresse != null ? adresse : new Adresse();
        this.articles = articles != null ? new ArrayList<>(articles) : new ArrayList<>();
        this.encheres = encheres != null ? new ArrayList<>(encheres) : new ArrayList<>();
    }

    // Getters & Setters

    public long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public List<Role> getRoles() {
        return new ArrayList<>(roles);
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public List<ArticleAVendre> getArticles() {
        return new ArrayList<>(articles);
    }

    public void setArticles(List<ArticleAVendre> articles) {
        this.articles = articles != null ? new ArrayList<>(articles) : new ArrayList<>();
    }

    public List<Enchere> getEncheres() {
        return new ArrayList<>(encheres);
    }

    public void setEncheres(List<Enchere> encheres) {
        this.encheres = encheres != null ? new ArrayList<>(encheres) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format(
                "Utilisateur [idUtilisateur=%s, pseudo=%s, nom=%s, prenom=%s, email=%s, telephone=%s, motDePasse=%s, credit=%s, roles=%s, adresse=%s, articles=%s, encheres=%s]",
                idUtilisateur, pseudo, nom, prenom, email, telephone, motDePasse, credit, roles, adresse, articles,
                encheres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adresse, articles, credit, email, encheres, idUtilisateur, motDePasse, nom, prenom, pseudo,
                roles, telephone);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Utilisateur other = (Utilisateur) obj;
        return Objects.equals(adresse, other.adresse) && Objects.equals(articles, other.articles)
                && credit == other.credit && Objects.equals(email, other.email)
                && Objects.equals(encheres, other.encheres) && idUtilisateur == other.idUtilisateur
                && Objects.equals(motDePasse, other.motDePasse) && Objects.equals(nom, other.nom)
                && Objects.equals(prenom, other.prenom) && Objects.equals(pseudo, other.pseudo)
                && Objects.equals(roles, other.roles) && Objects.equals(telephone, other.telephone);
    }
}
