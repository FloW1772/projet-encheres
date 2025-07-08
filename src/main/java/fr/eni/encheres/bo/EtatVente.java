package fr.eni.encheres.bo;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EtatVente {
    EN_COURS(0,"En cours"),
    TERMINEE(1,"Terminee"),
    ANNULEE(2,"Annulee"),
    NON_DEMARREE(3,"Non demarree");
	
private final int code;
    private final String label;

    EtatVente(int code, String label) {
    	this.code = code;
        this.label = label;
    }

    public int getCode() {
		return code;
	}

	public String getLabel() {
        return label;
    }
	public static EtatVente fromCode(int code) {
		for (EtatVente e : values()) {
			if(e.code == code) return e;
		}
		throw new IllegalArgumentException("Code EtatVente inconnu:" + code);
	}

    public static EtatVente fromString(String etatString) {
        for (EtatVente etat : EtatVente.values()) {
            if (etat.label.equalsIgnoreCase(etatString)) {
                return etat;
            }
        }
        throw new IllegalArgumentException("EtatVente inconnu: " + etatString);
    }
    
    public static EtatVente fromInt(int index) {
        EtatVente[] values = EtatVente.values();
        if (index < 0 || index >= values.length) {
            throw new IllegalArgumentException("Index EtatVente invalide: " + index);
        }
        return values[index];
    }
    
    public static Map<String, String> getEtatMap() {
        return Arrays.stream(EtatVente.values())
                     .collect(Collectors.toMap(Enum::name, EtatVente::getLabel));
    }
  
}
