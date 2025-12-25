package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.nugget.Ytiliti.Messages;
import java.util.List;

public class ping implements CommandHandler {
    @Override public String getName() { return "ping"; }
    @Override public String getDescription() { return "Пинг типа, есть же?"; }
    @Override public List<String> getAliases() { return List.of("pingashek", "test"); }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(Messages.getString("messages.ping.success")).queue();
    }
}