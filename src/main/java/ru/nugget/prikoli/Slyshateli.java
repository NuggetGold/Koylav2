package ru.nugget.prikoli;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// [ Берем текст от чувачокв ]
public class Slyshateli extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Стринги и остальная залупа
        String message = event.getMessage().getContentRaw();

        // Проверки на мурка.
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().equals("murk")) return; event.getMessage().reply("Муркам слово не давали");

        if (message.equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Сам.").queue();
        }
    }
}