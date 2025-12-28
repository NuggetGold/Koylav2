package ru.nugget;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ru.nugget.Ytiliti.Config;
import ru.nugget.Ytiliti.Messages;
import ru.nugget.prikoli.Slyshateli;
import ru.nugget.prikoli.WordSlushatel;


public class Main {
    public static void main(String[] args) {
        Config.loadConfig();
        Messages.loadConfig();
        String token = Config.getString("bot.token");

        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Slyshateli())
                .addEventListeners(new WordSlushatel())
                .build();
    }
}