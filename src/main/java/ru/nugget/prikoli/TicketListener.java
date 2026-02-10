package ru.nugget.prikoli;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.nugget.Ytiliti.Config;

import java.awt.*;
import java.util.EnumSet;

public class TicketListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("ticket:menu")) return;

        String userId = event.getUser().getId();
        String categoryType = event.getValues().get(0);

        // Получаем и обновляем счетчик тикетов
        JsonObject counters = Config.getJsonObject("tickets.counters");
        if (counters == null) counters = new JsonObject();

        int count = counters.has(userId) ? counters.get(userId).getAsInt() + 1 : 1;
        counters.addProperty(userId, count);
        Config.saveConfig();

        String categoryId = Config.getString("tickets.category_id");
        Category category = event.getGuild().getCategoryById(categoryId);
        String staffRoleId = Config.getString("tickets.staff_role_id");
        Role staffRole = event.getGuild().getRoleById(staffRoleId);

        String channelName = "ticket-" + event.getUser().getName() + "-" + count;

        event.getGuild().createTextChannel(channelName, category)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .addPermissionOverride(staffRole, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .queue(channel -> {
                    event.reply("✅ Тикет создан: " + channel.getAsMention()).setEphemeral(true).queue();

                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("🎫 Новый тикет: " + categoryType.toUpperCase())
                            .setDescription("Привет " + event.getUser().getAsMention() + "!\n" +
                                    "Опиши свой вопрос или проблему в этом чате.\n" +
                                    "Администрация " + (staffRole != null ? staffRole.getAsMention() : "") + " скоро ответит.")
                            .addField("Категория", categoryType, true)
                            .addField("Номер тикета", String.valueOf(count), true)
                            .setColor(Color.GREEN)
                            .setTimestamp(java.time.Instant.now());

                    channel.sendMessage(event.getUser().getAsMention() + " " + (staffRole != null ? staffRole.getAsMention() : ""))
                            .setEmbeds(eb.build())
                            .queue();
                });
    }
}