package com.exasol.releasedroid.formatting;

import static org.fusesource.jansi.Ansi.ansi;

import java.net.URL;

import org.fusesource.jansi.Ansi.Attribute;

public class Colorizer {

    /**
     * @param text text to colorize
     * @return text with ansi color red
     */
    public static String red(final String text) {
        return ansi().fgRed().a(text).reset().toString();
    }

    public static String green(final String text) {
        return ansi().fgGreen().a(text).reset().toString();
    }

    public static String formatLink(final URL url) {
        return ansi().fgBrightBlue().a(Attribute.UNDERLINE).a(url).reset().toString();
    }

    public static String brightGreen(final String s) {
        return ansi().fgBrightGreen().a(s).reset().toString();
    }

    public static String yellow(final String s) {
        return ansi().fgBrightYellow().a(s).reset().toString();
    }

}
