package ru.nugget.log;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nugget.Ytiliti.Config;
import ru.nugget.log.LoggerLogic;
import java.awt.*;

public class LoggerLogic {
    private static final Logger logger = LoggerFactory.getLogger(LoggerLogic.class);

    public static void SendInfo(Event event, String message) {
        try {
            if (event == null) {
                logger.info(message);
                return;
            }

            var InfoChannel = Config.getString("bot.log");
            if (InfoChannel == null || InfoChannel.isEmpty()) {
                logger.info(message);
                return;
            }

            TextChannel channel = event.getJDA().getTextChannelById(InfoChannel);
            if (channel == null) {
                logger.info(message);
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("\uD83D\uDCCB Лог [INFO]");
            eb.setDescription(message);
            eb.setColor(Color.decode("#Add8e6"));
            eb.setTimestamp(java.time.Instant.now());
            channel.sendMessageEmbeds(eb.build()).queue();
        } catch (Exception e) {
            logger.error("Ошибка в SendInfo: {}", e.getMessage());
        }
    }

    public static void SendWarn(Event event, String message) {
        try {
            if (event == null) {
                logger.warn(message);
                return;
            }

            var InfoChannel = Config.getString("bot.log");
            if (InfoChannel == null || InfoChannel.isEmpty()) {
                logger.warn(message);
                return;
            }

            TextChannel channel = event.getJDA().getTextChannelById(InfoChannel);
            if (channel == null) {
                logger.warn(message);
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("\uD83D\uDCCB Лог [WARN]");
            eb.setDescription(message);
            eb.setColor(Color.ORANGE);
            eb.setTimestamp(java.time.Instant.now());
            channel.sendMessageEmbeds(eb.build()).queue();
        } catch (Exception e) {
            logger.error("Ошибка в SendWarn: {}", e.getMessage());
        }
    }

    public static void SendCritical(Event event, String message) {
        try {
            if (event == null) {
                logger.error(message);
                return;
            }

            var InfoChannel = Config.getString("bot.log");
            if (InfoChannel == null || InfoChannel.isEmpty()) {
                logger.error(message);
                return;
            }

            TextChannel channel = event.getJDA().getTextChannelById(InfoChannel);
            if (channel == null) {
                logger.error(message);
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("\uD83D\uDCCB Лог [ERROR]");
            eb.setDescription(message);
            eb.setColor(Color.RED);
            eb.setTimestamp(java.time.Instant.now());
            channel.sendMessageEmbeds(eb.build()).queue();
        } catch (Exception e) {
            logger.error("Ошибка в SendCritical: {}", e.getMessage());
        }
    }
}