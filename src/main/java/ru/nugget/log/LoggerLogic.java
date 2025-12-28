package ru.nugget.log;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nugget.Ytiliti.Config;

import java.awt.*;

import static org.reflections.Reflections.log;

// [ Берем текст от чувачокв ]
public class LoggerLogic extends ListenerAdapter {
    public static final Logger logger = LoggerFactory.getLogger(LoggerLogic.class);

    public static void SendInfo(Event event, String message) {
        var InfoChannel = Config.getString("bot.log");
        TextChannel channel = event.getJDA().getTextChannelById(InfoChannel);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDCCB Лог [INFO]");
        eb.setDescription(message);
        eb.setColor(Color.decode("#Add8e6"));
        eb.setTimestamp(java.time.Instant.now());
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void SendWarn(Event event, String message) {
        var InfoChannel = Config.getString("bot.log");
        TextChannel channel = event.getJDA().getTextChannelById(InfoChannel);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDCCB Лог [WARN]");
        eb.setDescription(message);
        eb.setColor(Color.ORANGE);
        eb.setTimestamp(java.time.Instant.now());
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void SendCritical(Event event, String message) {
        var InfoChannel = Config.getString("bot.log");
        TextChannel channel = event.getJDA().getTextChannelById(InfoChannel);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDCCB Лог [ERROR]");
        eb.setDescription(message);
        eb.setColor(Color.RED);
        eb.setTimestamp(java.time.Instant.now());
        channel.sendMessageEmbeds(eb.build()).queue();
    }

}