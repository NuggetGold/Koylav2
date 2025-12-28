package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.nugget.Ytiliti.Config;
import java.util.*;

public class WordGame implements CommandHandler {
    public static final Map<String, String> currentGames = new HashMap<>();
    public static final Map<String, Set<String>> usedWords = new HashMap<>();

    @Override public String getName() { return "слово"; }
    @Override public String getDescription() { return "Игра слова"; }
    @Override public List<String> getAliases() { return List.of("word"); }

    @Override
    public void execute(MessageReceivedEvent event, List<String> args) {
        String channelId = event.getChannel().getId();
        String gameChannelId = Config.getString("bot.word");

        if (!channelId.equals(gameChannelId)) {
            event.getChannel().sendMessage("❌ Игра работает только в канале для слов!").queue();
            return;
        }

        if (args.isEmpty()) {
            showRules(event);
            return;
        }

        String command = args.get(0).toLowerCase();

        if (command.equals("начать")) {
            if (args.size() < 2) {
                event.getChannel().sendMessage("Укажите начальное слово: `f!слово начать город`").queue();
                return;
            }
            startGame(event, args.get(1), channelId);
        }
        else if (command.equals("стоп")) {
            endGame(event, channelId);
        }
        else if (command.equals("правила")) {
            showRules(event);
        }
        else if (command.equals("статус")) {
            showStatus(event, channelId);
        }
    }

    private void startGame(MessageReceivedEvent event, String startWord, String channelId) {
        startWord = startWord.toLowerCase().trim();

        if (currentGames.containsKey(channelId)) {
            event.getChannel().sendMessage("Игра уже идет! Текущее слово: **" + currentGames.get(channelId) + "**").queue();
            return;
        }

        if (startWord.length() < 2) {
            event.getChannel().sendMessage("Слово должно быть минимум из 2 букв!").queue();
            return;
        }

        currentGames.put(channelId, startWord);
        usedWords.put(channelId, new HashSet<>());
        usedWords.get(channelId).add(startWord);

        char nextLetter = getNextLetter(startWord);

        event.getChannel().sendMessage(
                "🎮 **Игра началась!**\n" +
                        "Первое слово: **" + startWord + "**\n" +
                        "Следующее слово должно начинаться на букву: **" + nextLetter + "**\n"
        ).queue();
    }

    public static boolean processWord(MessageReceivedEvent event, String word) {
        String channelId = event.getChannel().getId();
        String gameChannelId = Config.getString("bot.word");

        if (!channelId.equals(gameChannelId)) {
            return false;
        }
        if (!currentGames.containsKey(channelId)) {
            return false;
        }

        word = word.toLowerCase().trim();
        String lastWord = currentGames.get(channelId);
        char requiredLetter = getNextLetter(lastWord);

        if (!word.startsWith(String.valueOf(requiredLetter))) {
            event.getChannel().sendMessage(
                    "❌ **" + event.getAuthor().getName() + "**, неверно!\n" +
                            "Последнее слово: **" + lastWord + "**\n" +
                            "Нужна буква: **" + requiredLetter + "**\n" +
                            "Вы сказали: **" + word + "**"
            ).queue();
            return true;
        }

        if (usedWords.get(channelId).contains(word)) {
            event.getChannel().sendMessage("❌ Слово **" + word + "** уже использовалось!").queue();
            return true;
        }
        if (word.length() < 2) {
            event.getChannel().sendMessage("❌ Слово должно быть минимум из 2 букв!").queue();
            return true;
        }

        currentGames.put(channelId, word);
        usedWords.get(channelId).add(word);

        char nextLetter = getNextLetter(word);
        int wordCount = usedWords.get(channelId).size();

        event.getChannel().sendMessage(
                "✅ **" + event.getAuthor().getName() + "** сказал: **" + word + "**\n" +
                        "Следующая буква: **" + nextLetter + "**\n" +
                        "Всего слов: **" + wordCount + "**"
        ).queue();
        return true;
    }

    private static char getNextLetter(String word) {
        word = word.toLowerCase().trim();

        for (int i = word.length() - 1; i >= 0; i--) {
            char c = word.charAt(i);
            if (c != 'ь' && c != 'ъ' && c != 'ы') {
                return c;
            }
        }
        return word.charAt(word.length() - 1);
    }

    private void endGame(MessageReceivedEvent event, String channelId) {
        if (!currentGames.containsKey(channelId)) {
            event.getChannel().sendMessage("❌ Игра не начата!").queue();
            return;
        }

        int score = usedWords.get(channelId).size();
        String lastWord = currentGames.get(channelId);

        currentGames.remove(channelId);
        usedWords.remove(channelId);

        event.getChannel().sendMessage(
                "🏁 **Игра окончена!**\n" +
                        "Последнее слово: **" + lastWord + "**\n" +
                        "Всего названо слов: **" + score + "**\n" +
                        "Чтобы начать новую игру: `f!слово начать <слово>`"
        ).queue();
    }

    private void showStatus(MessageReceivedEvent event, String channelId) {
        if (!currentGames.containsKey(channelId)) {
            event.getChannel().sendMessage("❌ Игра не начата!").queue();
            return;
        }

        String lastWord = currentGames.get(channelId);
        char nextLetter = getNextLetter(lastWord);
        int wordCount = usedWords.get(channelId).size();

        event.getChannel().sendMessage(
                "📊 **Статус игры:**\n" +
                        "Текущее слово: **" + lastWord + "**\n" +
                        "Следующая буква: **" + nextLetter + "**\n" +
                        "Всего слов: **" + wordCount + "**"
        ).queue();
    }

    private void showRules(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                "📖 **Правила игры 'Слова':**\n" +
                        "1. Начните игру: `f!слово начать <слово>`\n" +
                        "2. Следующий игрок пишет слово на последнюю букву предыдущего\n" +
                        "3. Буквы 'ь', 'ъ', 'ы' пропускаются\n" +
                        "4. Слова не должны повторяться\n" +
                        "5. Чтобы закончить: `f!слово стоп`\n"
        ).queue();
    }
}