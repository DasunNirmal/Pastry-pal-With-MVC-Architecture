package lk.ijse.PastryPal.RegExPatterns;

import lombok.Getter;

import java.util.regex.Pattern;

public class RegExPatterns {
    @Getter
    public static final Pattern validName = Pattern.compile("\\b[A-Z][a-z]*( [A-Z][a-z]*)*\\b");
    @Getter
    public static final Pattern validPassword = Pattern.compile("(.*?[0-9]){4,}");

}
