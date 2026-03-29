package menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: this class describes an application menu with "buttons"
 */
@Getter
@RequiredArgsConstructor
public enum Menu {
    CREATE("create user"),
    READ("read user"),
    READ_ALL("read all users"),
    UPDATE("update user"),
    DELETE("delete user"),
    EXIT("exit");

    private final String description;

    /**
     * @ Method Name: getOrdinal
     * @ Description: getting Menu by the int value
     * @ param -> return: [int] [ordinal] -> menu.Menu
     */
    public static Menu getOrdinal(int ordinal) {
        return Menu.values()[ordinal];
    }

    /**
     * @ Method Name: toString
     * @ Description: getting the String value for the Menu
     * @ param -> return: [] [] -> java.lang.String
     */
    @Override
    public String toString() {
        return String.format("%s %-50s button %d", "If you want to move on to",
                getDescription(), ordinal() + 1);
    }
}
