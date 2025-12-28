package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.nugget.Ytiliti.Messages;
import ru.nugget.log.LoggerLogic;
import java.util.List;

public class test implements CommandHandler {
    @Override public String getName() { return "test"; }
    @Override public String getDescription() { return "Тест команда"; }
    @Override public List<String> getAliases() { return List.of("test"); }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(Messages.getString("messages.ping.success")).queue();
        LoggerLogic.SendInfo(event, "тестовая инфа");
        LoggerLogic.SendWarn(event, "тестовый варн");
        LoggerLogic.SendCritical(event, "тестовый критичный баг/ошибка/поломка.");
    }
}