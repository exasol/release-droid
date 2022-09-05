package com.exasol.releasedroid.formatting;

import static org.fusesource.jansi.Ansi.ansi;

import java.net.URL;

import org.fusesource.jansi.Ansi.Attribute;

public class Colorizer {

    private Colorizer() {
        // only static usage
    }

    /**
     * @param text text to colorize
     * @return text with ansi color red
     */
    public static String red(final String text) {
        return ansi().fgBrightRed().a(text).reset().toString();
    }

    public static String green(final String text) {
        return ansi().fgBrightGreen().a(text).reset().toString();
    }

    public static String formatLink(final URL url) {
        return formatLink(url.toString());
    }

    public static String formatLink(final String url) {
        return ansi().fgBrightBlue().a(Attribute.UNDERLINE).a(url).reset().toString();
    }

    public static String brightGreen(final String s) {
        return ansi().fgBrightGreen().a(s).reset().toString();
    }

    public static String yellow(final String s) {
        return ansi().fgBrightYellow().a(s).reset().toString();
    }
}
