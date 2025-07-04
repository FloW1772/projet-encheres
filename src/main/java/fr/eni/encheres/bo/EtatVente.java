package fr.eni.encheres.bo;
	
public enum EtatVente {
	EN_COURS("En cours"),
    TERMINEE("Terminee"),
    ANNULEE("Annulee"),
    NON_DEMARREE("Non demarree");

    private final String label;

    EtatVente(String label) {
        this.label = label;
    }

    public static EtatVente fromString(String etatString) {
        for (EtatVente etat : EtatVente.values()) {
            if (etat.label.equalsIgnoreCase(etatString)) {
                return etat;
            }
        }
        throw new IllegalArgumentException("EtatVente inconnu: " + etatString);
    }
}