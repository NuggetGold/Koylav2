package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nugget.Ytiliti.Config;
import ru.nugget.log.LoggerLogic;

import java.util.*;

import static ru.nugget.log.LoggerLogic.SendInfo;

public class CommandManager {
    private static final Logger log = LoggerFactory.getLogger(CommandManager.class);
    private final Map<String, CommandHandler> commands = new HashMap<>();

    public void registerCommand(CommandHandler cmd) {
        try {
            commands.put(cmd.getName().toLowerCase(), cmd);
            for (String alias : cmd.getAliases()) {
                commands.put(alias.toLowerCase(), cmd);
            }
            log.info("Команда '{}' зарегистрирована (алиасы: {})",
                    cmd.getName(), String.join(", ", cmd.getAliases()));
        } catch (Exception e) {
            log.error("Ошибка при регистрации команды: {}", e.getMessage(), e);
            LoggerLogic.SendCritical(null, "Ошибка регистрации команды: " + e.getMessage());
        }
    }

    public void handle(MessageReceivedEvent event, String prefix) {
        String content = event.getMessage().getContentRaw();
        if (!content.startsWith(prefix)) {
            return;
        }

        try {
            String[] split = content.replaceFirst("(?i)" + prefix, "").split("\\s+");
            if (split.length == 0) {
                return;
            }

            String commandName = split[0].toLowerCase();
            CommandHandler cmd = commands.get(commandName);

            if (cmd != null) {
                List<String> args = Arrays.asList(split).subList(1, split.length);
                cmd.execute(event, args);

            } else {
                log.warn("Попытка выполнения неизвестной команды '{}' пользователем {}",
                        commandName, event.getAuthor().getAsTag());

                event.getChannel().sendMessage("❌ Неизвестная команда: `" + commandName + "`").queue();
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке команды: {}", e.getMessage(), e);

            String errorMsg = String.format("Ошибка в команде от %s: %s",
                    event.getAuthor().getAsTag(), e.getMessage());
            LoggerLogic.SendCritical(event, errorMsg);
            event.getChannel().sendMessage("❌ Произошла ошибка при выполнении команды").queue();
        }
    }

    public void loadCommands() {
        try {
            Reflections reflections = new Reflections("ru.nugget.commands");
            Set<Class<? extends CommandHandler>> classes = reflections.getSubTypesOf(CommandHandler.class);

            SendInfo(null, "Начинаю загрузку команд...");

            int loaded = 0;
            for (Class<? extends CommandHandler> clazz : classes) {
                try {
                    CommandHandler cmd = clazz.getDeclaredConstructor().newInstance();
                    registerCommand(cmd);
                    loaded++;
                    log.debug("Загружена команда: {}", cmd.getName());

                } catch (Exception e) {
                    log.error("Ошибка при создании экземпляра команды {}: {}",
                            clazz.getSimpleName(), e.getMessage(), e);

                    String errorMsg = String.format("Ошибка загрузки команды %s: %s",
                            clazz.getSimpleName(), e.getMessage());
                    LoggerLogic.SendCritical(null, errorMsg);
                }
            }

            log.info("Загрузка команд завершена. Успешно загружено: {}/{}",
                    loaded, classes.size());

        } catch (Exception e) {
            log.error("Критическая ошибка при загрузке команд: {}", e.getMessage(), e);
            LoggerLogic.SendCritical(null, "Критическая ошибка загрузки команд: " + e.getMessage());
        }
    }

    public void reload() {
        try {
            log.info("Начинаю перезагрузку команд...");
            int previousCount = commands.size();
            commands.clear();
            loadCommands();
            log.info("Перезагрузка команд завершена. Было: {}, стало: {}",
                    previousCount, commands.size());

        } catch (Exception e) {
            log.error("Ошибка при перезагрузке команд: {}", e.getMessage(), e);
            LoggerLogic.SendCritical(null, "Ошибка перезагрузки команд: " + e.getMessage());
        }
    }
}