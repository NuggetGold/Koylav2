package ru.nugget.prikoli;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.nugget.Ytiliti.Config;

public class MurkDetector extends ListenerAdapter {

    public static boolean isEblan(User user) {
        JsonObject eblans = Config.getJsonObject("eblans");
        if (eblans == null) return false;

        String userId = user.getId();

        return eblans.entrySet().stream()
                .map(entry -> entry.getValue().getAsJsonObject())
                .anyMatch(person -> person.get("id").getAsString().equals(userId));
    }
}
