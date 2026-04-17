package command.center.CLI;

public final class ConsoleHelper {

    private ConsoleHelper() {} // prevent instantiation

    public static String padRight(String text, int width) {
        if (text == null) text = "";

        int visible = visibleLength(text);
        if (visible >= width) {
            return truncateAnsi(text, width);
        }

        return text + " ".repeat(width - visible);
    }

    public static String center(String text, int width) {
        if (text == null) text = "";

        int visible = visibleLength(text);
        if (visible >= width) {
            return truncateAnsi(text, width);
        }

        int totalPadding = width - visible;
        int left = totalPadding / 2;
        int right = totalPadding - left;

        return " ".repeat(left) + text + " ".repeat(right);
    }

    public static String padText(String text, int width) {
        if (text == null) text = "";

        int visible = visibleLength(text);
        if (visible > width) {
            return truncateAnsi(text, width - 1) + "~";
        }

        return text + " ".repeat(width - visible);
    }

    public static int visibleLength(String text) {
        return stripAnsi(text).length();
    }

    private static String stripAnsi(String text) {
        return text.replaceAll("\\u001B\\[[;\\d]*m", "");
    }

    private static String truncateAnsi(String text, int maxVisibleChars) {
        StringBuilder result = new StringBuilder();
        int visibleCount = 0;

        for (int i = 0; i < text.length();) {
            char ch = text.charAt(i);

            if (ch == '\u001B') {
                int end = i + 1;
                while (end < text.length() && text.charAt(end) != 'm') {
                    end++;
                }
                if (end < text.length()) end++;

                result.append(text, i, end);
                i = end;
            } else {
                if (visibleCount >= maxVisibleChars) break;

                result.append(ch);
                visibleCount++;
                i++;
            }
        }

        if (!result.toString().endsWith(Colors.RESET)) {
            result.append(Colors.RESET);
        }

        return result.toString();
    }
    public static int clampToGrid(int value, int grid_size) {
        return Math.max(0, Math.min(value, grid_size - 1));
    }
}