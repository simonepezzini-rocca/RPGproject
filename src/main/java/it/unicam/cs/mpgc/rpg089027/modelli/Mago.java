package it.unicam.cs.mpgc.rpg089027.modelli;

/**
 * Rappresenta un eroe specializzato nelle arti magiche.
 * <p>
 * Dispone di una riserva di salute inferiore rispetto ad altre classi,
 * ma compensa con un elevato potenziale offensivo alimentato dal mana.
 */
public class Mago extends Eroe {

    private int mana;
    private final int manaMassimo;
    private int potenzaMagica;

    /**
     * Costruisce una nuova istanza di Mago.
     *
     * @param nome          il nome del mago
     * @param potenzaMagica il valore di potenza degli incantesimi (maggiore di zero)
     * @param manaMassimo   la capienza massima del serbatoio di mana (maggiore di zero)
     */
    public Mago(String nome, int potenzaMagica, int manaMassimo) {
        super(nome, 80); // Punti vita base più contenuti per il mago
        if (potenzaMagica <= 0) {
            throw new IllegalArgumentException("La potenza magica deve essere maggiore di zero.");
        }
        if (manaMassimo <= 0) {
            throw new IllegalArgumentException("Il mana massimo deve essere maggiore di zero.");
        }
        this.potenzaMagica = potenzaMagica;
        this.manaMassimo = manaMassimo;
        this.mana = manaMassimo;
    }

    public int getMana() {
        return mana;
    }

    public int getManaMassimo() {
        return manaMassimo;
    }

    public int getPotenzaMagica() {
        return potenzaMagica;
    }

    public void ricaricaMana(int quantita) {
        if (quantita > 0) {
            this.mana = Math.min(this.manaMassimo, this.mana + quantita);
        }
    }

    @Override
    public int calcolaDannoAttacco() {
        // Attacco base con bastone magico scalato sulla potenza magica
        return (this.potenzaMagica / 2) + (getLivello() * 2);
    }

    @Override
    public String eseguiAbilitaSpeciale(Combattente bersaglio) {
        if (bersaglio == null) {
            throw new IllegalArgumentException("Il bersaglio dell'abilità speciale non può essere nullo.");
        }
        final int costoMana = 20;

        if (this.mana >= costoMana) {
            this.mana -= costoMana;
            int dannoMagico = (this.potenzaMagica * 2) + 10;
            bersaglio.subisciDanno(dannoMagico);
            return String.format("%s lancia una devastante 'Palla di Fuoco' su %s infliggendo %d danni magici! (Mana residuo: %d/%d)",
                    getNome(), bersaglio.getNome(), dannoMagico, this.mana, this.manaMassimo);
        } else {
            // Mana insufficiente: attacco d'emergenza con rigenerazione
            ricaricaMana(10);
            int dannoMinimo = 5;
            bersaglio.subisciDanno(dannoMinimo);
            return String.format("%s non ha abbastanza mana per l'incantesimo! Colpisce con il bastone infliggendo %d danni e recupera 10 mana.",
                    getNome(), dannoMinimo);
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Mago - Mana: %d/%d, Potenza: %d]", mana, manaMassimo, potenzaMagica);
    }
}