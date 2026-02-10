package ru.nugget.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import ru.nugget.Ytiliti.Config;

import java.awt.*;
import java.util.List;

public class TicketSetupCommand implements CommandHandler {
    @Override public String getName() { return "ticket-setup"; }
    @Override public String getDescription() { return "Установка системы тикетов"; }
    @Override public List<String> getAliases() { return List.of("tsetup"); }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        if (!event.getMember().hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) return;

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🎫 Создание тикета")
                .setDescription("Тут вы можете создать тикет для связи с администрацией.\nВыберите категорию ниже:")
                .setColor(Color.CYAN)
                .setFooter("Система поддержки NuggetBot");

        StringSelectMenu menu = StringSelectMenu.create("ticket:menu")
                .setPlaceholder("Выберите причину обращения...")
                .addOption("Помощь", "help", "Помочь со сборкой заебал.")
                .addOption("Вопрос", "question", "Задать вопрос по боту/серверу")
                .addOption("Жалоба", "report", "Подать жалобу на игрока")
                .addOption("Другое", "other", "Иные вопросы")
                .build();

        event.getChannel().sendMessageEmbeds(eb.build())
                .addActionRow(menu)
                .queue();
    }
}