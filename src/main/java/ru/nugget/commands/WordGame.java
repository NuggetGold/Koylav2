package ru.nugget.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.nugget.Ytiliti.Config;
import ru.nugget.Ytiliti.RoleChecker;
import ru.nugget.prikoli.MurkDetector;

import java.time.temporal.ValueRange;
import java.util.*;

import static ru.nugget.log.LoggerLogic.SendInfo;

public class WordGame implements CommandHandler {
    public static final Map<String, String> currentGames = new HashMap<>();
    public static final Map<String, Set<String>> usedWords = new HashMap<>();

    private static final int MIN_SIMILARITY_LENGTH = 3;
    private static final int MAX_EDIT_DISTANCE = 2;

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

        if (!startWord.matches("[а-яё]+")) {
            event.getChannel().sendMessage("❌ Слово должно содержать только русские буквы!").queue();
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

        if (MurkDetector.isEblan(event.getAuthor())) {
            SendInfo(event, event.getAuthor().getName() + " Попытался сыграть но соснул хуйца");
            return false;
        }
        String bannedRoleIds = Config.getString("word.banned_roles");

        if (RoleChecker.hasBannedRole(event.getMember(), Collections.singletonList(bannedRoleIds))) {
            SendInfo(event, event.getAuthor().getName() + " Попытался сыграть но соснул хуйца");
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

        if (word.length() < 2) {
            event.getChannel().sendMessage("❌ Слово должно быть минимум из 2 букв!").queue();
            return true;
        }

        if (!word.matches("[а-яё]+")) {
            event.getChannel().sendMessage("❌ Слово должно содержать только русские буквы!").queue();
            return true;
        }

        if (usedWords.get(channelId).contains(word)) {
            event.getChannel().sendMessage("❌ Слово **" + word + "** уже использовалось!").queue();
            return true;
        }

        String similarityCheck = checkWordSimilarity(word, usedWords.get(channelId));
        if (similarityCheck != null) {
            event.getChannel().sendMessage(
                    "❌ Слово **" + word + "** слишком похоже на **" + similarityCheck + "**!\n" +
                            "⚠️ Слова не должны быть похожими по:\n" +
                            "• Окончанию\n" +
                            "• Началу\n" +
                            "• Содержанию одного в другом\n" +
                            "• Быть почти одинаковыми"
            ).queue();
            return true;
        }

        if (isTooSimilar(lastWord, word)) {
            event.getChannel().sendMessage(
                    "❌ Слово **" + word + "** слишком похоже на предыдущее **" + lastWord + "**!\n" +
                            "Попробуйте придумать менее похожее слово."
            ).queue();
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


    /**
     * Проверяет, не слишком ли слово похоже на предыдущие слова
     */
    private static String checkWordSimilarity(String newWord, Set<String> usedWords) {
        for (String usedWord : usedWords) {
            if (isTooSimilar(usedWord, newWord)) {
                return usedWord;
            }
        }
        return null;
    }

    /**
     * Основная проверка похожести слов взял с инета
     */
    public static boolean isTooSimilar(String word1, String word2) {
        if (word1 == null || word2 == null) return false;

        word1 = word1.toLowerCase().trim();
        word2 = word2.toLowerCase().trim();

        if (word1.equals(word2)) {
            return true;
        }

        if (word1.contains(word2) || word2.contains(word1)) {
            return true;
        }

        int minLength = Math.min(word1.length(), word2.length());
        if (minLength >= MIN_SIMILARITY_LENGTH) {
            String start1 = word1.substring(0, MIN_SIMILARITY_LENGTH);
            String start2 = word2.substring(0, MIN_SIMILARITY_LENGTH);
            if (start1.equals(start2)) {
                return true;
            }

            String end1 = word1.substring(word1.length() - Math.min(MIN_SIMILARITY_LENGTH, word1.length()));
            String end2 = word2.substring(word2.length() - Math.min(MIN_SIMILARITY_LENGTH, word2.length()));
            if (end1.equals(end2)) {
                return true;
            }
        }

        if (getLevenshteinDistance(word1, word2) <= MAX_EDIT_DISTANCE) {
            return true;
        }
        if (areAnagrams(word1, word2)) {
            return true;
        }

        return false;
    }

    /**
     * Вычисление расстояния Левенштейна взял с инета
     */
    private static int getLevenshteinDistance(String s1, String s2) {
        if (Math.abs(s1.length() - s2.length()) > MAX_EDIT_DISTANCE) {
            return MAX_EDIT_DISTANCE + 1;
        }

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    /**
     * Проверка, являются ли слова анаграммами
     */
    private static boolean areAnagrams(String word1, String word2) {
        if (word1.length() != word2.length()) return false;

        char[] chars1 = word1.toCharArray();
        char[] chars2 = word2.toCharArray();
        Arrays.sort(chars1);
        Arrays.sort(chars2);

        return Arrays.equals(chars1, chars2);
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
                        "4. **Слова не должны повторяться**\n" +
                        "5. **Слова не должны быть похожи на предыдущие!** (по окончанию, началу и т.д.)\n" +
                        "6. Чтобы закончить: `f!слово стоп`\n" +
                        "7. Текущий статус: `f!слово статус`\n"
        ).queue();
    }
}