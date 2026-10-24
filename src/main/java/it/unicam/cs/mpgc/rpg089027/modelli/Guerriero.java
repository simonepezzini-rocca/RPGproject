package it.unicam.cs.mpgc.rpg089027.modelli;

/**
 * Rappresenta un eroe specializzato nel combattimento corpo a corpo.
 * <p>
 * Caratterizzato da un'elevata riserva di salute e da una forza fisica
 * che potenzia i danni inflitti e alimenta l'abilità speciale.
 */
public class Guerriero extends Eroe {

    private int forzaFisica;

    /**
     * Costruisce una nuova istanza di Guerriero con parametri bilanciati.
     *
     * @param nome        il nome del guerriero
     * @param forzaFisica il valore di forza fisica (deve essere maggiore di zero)
     */
    public Guerriero(String nome, int forzaFisica) {
        super(nome, 120); // Punti vita base elevati per il guerriero
        if (forzaFisica <= 0) {
            throw new IllegalArgumentException("La forza fisica del guerriero deve essere maggiore di zero.");
        }
        this.forzaFisica = forzaFisica;
    }

    public int getForzaFisica() {
        return forzaFisica;
    }

    public void incrementaForza(int incremento) {
        if (incremento > 0) {
            this.forzaFisica += incremento;
        }
    }

    @Override
    public int calcolaDannoAttacco() {
        // Danno base scalato sulla forza e sul livello
        return this.forzaFisica + (getLivello() * 3);
    }

    @Override
    public String eseguiAbilitaSpeciale(Combattente bersaglio) {
        if (bersaglio == null) {
            throw new IllegalArgumentException("Il bersaglio dell'abilità speciale non può essere nullo.");
        }
        int dannoSpeciale = calcolaDannoAttacco() + 15;
        bersaglio.subisciDanno(dannoSpeciale);
        return String.format("%s sferra un 'Fendente Poderoso' contro %s infliggendo %d danni fisici!",
                getNome(), bersaglio.getNome(), dannoSpeciale);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Guerriero - Forza: %d]", forzaFisica);
    }
}
