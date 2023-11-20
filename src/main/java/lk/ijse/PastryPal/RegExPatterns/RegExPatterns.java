package lk.ijse.PastryPal.RegExPatterns;


import lombok.Getter;

import java.util.regex.Pattern;

public class RegExPatterns {
    @Getter
    public static final Pattern validName = Pattern.compile("\\b[A-Z][a-z]*( [A-Z][a-z]*)*\\b");
    @Getter
    public static final Pattern validDescription = Pattern.compile("\\b[a-z.A-Z]+(?: [a-zA-Z]+)*\\b");
    @Getter
    public static final Pattern validPassword = Pattern.compile("(.*?[0-9]){4,}");
    @Getter
    public static final Pattern validPhoneNumber = Pattern.compile("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$");
    @Getter
    public static final Pattern validAddress = Pattern.compile("^[A-Za-z0-9'\\/\\.\\,\\s]{5,}$");
    @Getter
    public static final Pattern validDouble = Pattern.compile("^[0-9]+\\.?[0-9]*$");
}
