package org.welcome;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Constants {
    public static final String DEV_SERVER_ID = "149912376387436544";

    private static final String datePattern = "dd-MM-yyyy HH:mm";
    public static final DateTimeFormatter standardDateFormatter = DateTimeFormatter.ofPattern(datePattern);
    public static final DateTimeFormatter localDateFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                    .withLocale(Locale.UK)
                    .withZone(ZoneId.systemDefault());

}
