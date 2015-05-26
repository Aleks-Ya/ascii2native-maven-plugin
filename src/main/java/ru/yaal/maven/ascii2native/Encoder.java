package ru.yaal.maven.ascii2native;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yablokov a.
 */
public class Encoder {
    static String natives = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    static Map map = new HashMap();
//todo избавиться от map
    static {
        for (int i = 0; i < natives.length(); i++) {
            Character c = new Character(natives.charAt(i));
            StringBuilder sb = new StringBuilder("\\u");
            String hex = Integer.toHexString(c.charValue());
            for (int j = hex.length(); j < 4; j++) {
                sb.append('0');
            }
            sb.append(hex);
            map.put(sb.toString(), c);
        }
    }

    public static Character ascii2native(String c) {
        return (Character) map.get(c);
    }
}
