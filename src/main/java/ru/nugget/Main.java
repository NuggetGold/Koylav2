package ru.nugget;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.JDA;
import ru.nugget.Ytiliti.Config;
import ru.nugget.Ytiliti.Messages;
import ru.nugget.prikoli.Slyshateli;
import ru.nugget.prikoli.TicketListener;
import ru.nugget.prikoli.WordSlushatel;

public class Main {
    public static void main(String[] args) {
        try {
            Config.loadConfig();
            Messages.loadConfig();
            String token = Config.getString("bot.token");

            JDA jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new Slyshateli())
                    .addEventListeners(new WordSlushatel())
                    .addEventListeners(new TicketListener())
                    .build();

            jda.awaitReady();
            sendStartupMessage(jda);

        } catch (Exception e) {
            System.err.println("❌ Ошибка запуска бота: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendStartupMessage(JDA jda) {
        String logChannelId = Config.getString("bot.log");

        try {
            var channel = jda.getTextChannelById(logChannelId);
            var embed = new net.dv8tion.jda.api.EmbedBuilder()
                    .setTitle("🟢 Бот запущен")
                    .setColor(0x00FF00)
                    .addField("Версия", Config.getString("bot.version"), true)
                    .addField("Префикс", Config.getString("settings.prefix"), true)
                    .addField("Создатель", Config.getString("bot.creator"), true)
                    .addField("Серверов", String.valueOf(jda.getGuilds().size()), true)
                    .addField("Время запуска", java.time.LocalDateTime.now().toString(), false)
                    .setFooter("ID: " + jda.getSelfUser().getId())
                    .build();

            channel.sendMessageEmbeds(embed).queue(
                    success -> System.out.println("📨 Сообщение о запуске отправлено в лог-канал"),
                    error -> System.err.println("❌ Ошибка отправки сообщения: " + error.getMessage())
            );

        } catch (Exception e) {
            System.err.println("❌ Ошибка при отправке сообщения о запуске: " + e.getMessage());
        }
    }
}