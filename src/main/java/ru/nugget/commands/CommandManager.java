package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.reflections.Reflections;

import java.util.*;

public class CommandManager {
    private final Map<String, CommandHandler> commands = new HashMap<>();

    public void registerCommand(CommandHandler cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
        for (String alias : cmd.getAliases()) {
            commands.put(alias.toLowerCase(), cmd);
        }
    }

    public void handle(MessageReceivedEvent event, String prefix) {
        String content = event.getMessage().getContentRaw();
        if (!content.startsWith(prefix)) return;

        String[] split = content.replaceFirst("(?i)" + prefix, "").split("\\s+");
        String commandName = split[0].toLowerCase();

        CommandHandler cmd = commands.get(commandName);
        if (cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);
            cmd.execute(event, args);
        }
    }

    public void loadCommands() {
        Reflections reflections = new Reflections("ru.nugget.commands");
        Set<Class<? extends CommandHandler>> classes = reflections.getSubTypesOf(CommandHandler.class);

        for (Class<? extends CommandHandler> s : classes) {
            try {
                CommandHandler cmd = s.getDeclaredConstructor().newInstance();
                registerCommand(cmd);
                System.out.println("Загружена команда: " + cmd.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        commands.clear();
    }
}