package com.yn.anime.work.util;

/*
    classDescription:
*/
public class RoleUtils {

    public static boolean isAdmin(String roles) {

        if (roles == null) {
            return false;
        }

        return roles.contains("ADMIN");
    }
}
