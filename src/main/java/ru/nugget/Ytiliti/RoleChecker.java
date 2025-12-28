package ru.nugget.Ytiliti;

import net.dv8tion.jda.api.entities.Member;
import java.util.List;

public class RoleChecker {

    public static boolean hasBannedRole(Member member, List<String> bannedRoleIds) {
        if (member == null || bannedRoleIds == null || bannedRoleIds.isEmpty()) {
            return false;
        }

        return member.getRoles().stream()
                .map(role -> role.getId())
                .anyMatch(bannedRoleIds::contains);
    }
}