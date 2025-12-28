package ru.nugget.prikoli;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.nugget.Ytiliti.Config;
import ru.nugget.commands.WordGame;

public class WordSlushatel extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getContentRaw().isEmpty()) return;

        String channelId = event.getChannel().getId();
        String gameChannelId = Config.getString("bot.word");
        if (!channelId.equals(gameChannelId)) {
            return;
        }

        String message = event.getMessage().getContentRaw().trim();
        String prefix = Config.getString("settings.prefix");
        if (message.startsWith(prefix)) {
            return;
        }
        if (isValidWord(message)) {
            boolean processed = WordGame.processWord(event, message);
            if (processed) {
                event.getMessage().delete().queue();
            }
        }
    }

    private boolean isValidWord(String text) {
        String[] words = text.split("\\s+");
        if (words.length != 1) return false;

        String word = words[0];
        return word.matches("[а-яА-ЯёЁ]+") && word.length() >= 2;
    }
}
