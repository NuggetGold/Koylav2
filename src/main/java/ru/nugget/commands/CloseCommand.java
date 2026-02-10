package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CloseCommand implements CommandHandler {
    @Override public String getName() { return "close"; }
    @Override public String getDescription() { return "Закрыть текущий тикет"; }
    @Override public List<String> getAliases() { return List.of("закрыть", "ticket-close"); }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (!event.getChannel().getName().startsWith("ticket-")) {
            event.getChannel().sendMessage("❌ Эту команду можно использовать только в тикете!").queue();
            return;
        }

        event.getChannel().sendMessage("🔒 Тикет будет закрыт и удален через 5 секунд...").queue();

        event.getChannel().delete().queueAfter(5, TimeUnit.SECONDS);
    }
}