package fr.eni.encheres.bo;

import java.util.Objects;

public class Adresse {

    private long idAdresse = -1;
    private String rue;
    private String codePostal;
    private String ville;
    private String pays;
    private long idUtilisateur;

    public Adresse() {
    }

    public Adresse(long idAdresse, String rue, String codePostal, String ville, String pays, long idUtilisateur) {
        this.idAdresse = idAdresse;
        this.rue = rue;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
        this.idUtilisateur = idUtilisateur;
    }

    public long getIdAdresse() {
        return idAdresse;
    }

    public void setIdAdresse(long idAdresse) {
        this.idAdresse = idAdresse;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }


    @Override
    public String toString() {
        return "Adresse{" +
                "idAdresse=" + idAdresse +
                ", rue='" + rue + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", ville='" + ville + '\'' +
                ", pays='" + pays + '\'' +
                ", idUtilisateur=" + idUtilisateur +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Adresse adresse = (Adresse) o;
        return idAdresse == adresse.idAdresse && idUtilisateur == adresse.idUtilisateur && Objects.equals(rue, adresse.rue) && Objects.equals(codePostal, adresse.codePostal) && Objects.equals(ville, adresse.ville) && Objects.equals(pays, adresse.pays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAdresse, rue, codePostal, ville, pays, idUtilisateur);
    }
}
