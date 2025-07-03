package fr.eni.encheres.bo;
	
public enum EtatVente {
    EN_COURS,
    TERMINEE,
    ANNULEE,
    NON_DEMARREE;

    public static EtatVente fromInt(int etatInt) {
        switch (etatInt) {
            case 0: return EN_COURS;
            case 1: return TERMINEE;
            case 2: return ANNULEE;
            case 3: return NON_DEMARREE;
            default: throw new IllegalArgumentException("EtatVente inconnu: " + etatInt);
        }
    }
}