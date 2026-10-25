package it.unicam.cs.mpgc.rpg089027.persistenza;

import com.google.gson.*;
import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;

import java.lang.reflect.Type;

/**
 * Adattatore personalizzato per la libreria Gson che gestisce la serializzazione
 * e deserializzazione polimorfica della gerarchia della classe astratta {@link Eroe}.
 * <p>
 * Inietta una proprietà discriminatrice ("tipo") nel documento JSON per identificare
 * univocamente la classe concreta (es. Guerriero, Mago) in fase di ripristino.
 */
public class EroeTypeAdapter implements JsonSerializer<Eroe>, JsonDeserializer<Eroe> {

    private static final String PROPRIETA_TIPO = "tipo";

    @Override
    public JsonElement serialize(Eroe eroe, Type typeOfSrc, JsonSerializationContext context) {
        if (eroe == null) {
            return JsonNull.INSTANCE;
        }

        // Serializza l'oggetto concreto preservando tutti i campi
        JsonElement elemento = context.serialize(eroe, eroe.getClass());
        JsonObject jsonObject = elemento.getAsJsonObject();

        // Inserisce il marcatore di tipo esplicito
        jsonObject.addProperty(PROPRIETA_TIPO, eroe.getClass().getSimpleName());

        return jsonObject;
    }

    @Override
    public Eroe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has(PROPRIETA_TIPO)) {
            throw new JsonParseException("Proprietà discriminatrice '" + PROPRIETA_TIPO + "' mancante nel JSON dell'eroe.");
        }

        String tipo = jsonObject.get(PROPRIETA_TIPO).getAsString();

        return switch (tipo) {
            case "Guerriero" -> context.deserialize(jsonObject, Guerriero.class);
            case "Mago" -> context.deserialize(jsonObject, Mago.class);
            default -> throw new JsonParseException("Tipo di eroe non riconosciuto durante la deserializzazione: " + tipo);
        };
    }
}