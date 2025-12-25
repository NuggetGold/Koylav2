package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.List;

public interface CommandHandler {
    String getName();
    String getDescription();
    List<String> getAliases();

    void execute(MessageReceivedEvent event, List<String> args);
}