package ru.nugget.prikoli;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.nugget.Ytiliti.Config;
import ru.nugget.commands.CommandManager;


import static ru.nugget.log.LoggerLogic.SendInfo;

// [ Берем текст от чувачокв ]
public class Slyshateli extends ListenerAdapter {
    private final CommandManager manager = new CommandManager();

    public Slyshateli() {
        manager.loadCommands(); // Загружаем при старте
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Стринги и остальная залупа

        String message = event.getMessage().getContentRaw();

        // Проверки на мурка.
        if (event.getAuthor().isBot()) return;
        if (MurkDetector.isEblan(event.getAuthor())) {
            event.getMessage().reply("Муркам и хублотам слово не давали.");
            SendInfo(event, "Попытался выпленуть: "+ message);
            return;
        }

        if (event.getAuthor().isBot()) return;

        String prefix = Config.getString("settings.prefix");
        manager.handle(event, prefix);
    }
}