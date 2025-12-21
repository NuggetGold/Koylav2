package ru.nugget.prikoli;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// [ Берем текст от чувачокв ]
public class Slyshateli extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Стринги и остальная залупа
        String message = event.getMessage().getContentRaw();
        MurkDetector murkDetector = null;

        // Проверки на мурка.
        if (event.getAuthor().isBot()) return;
        if (murkDetector.isEblan(event.getAuthor())) {
            event.getMessage().reply("Муркам и хублотам слово не давали.");
            return;
        }

        if (message.equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Сам.").queue();
        }
    }
}