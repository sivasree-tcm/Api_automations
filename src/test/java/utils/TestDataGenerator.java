package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {

    private static final String ALPHABETS = "abcdefghijklmnopqrstuvwxyz";
    private static final Random random = new Random();

    private static String randomWord(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABETS.charAt(random.nextInt(ALPHABETS.length())));
        }
        return sb.toString();
    }
    public static String generateFutureDate() {
        return LocalDate.now()
                .plusDays(30)
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }



    // ✅ Org Name → only letters
    public static String generateOrgName() {
        return "Org" + capitalize(randomWord(6));
    }

    // ✅ Space Name → only letters
    public static String generateSpaceName() {
        return "Space" + capitalize(randomWord(6));
    }

    // ✅ Site URL → valid URL + only letters
    public static String generateSiteUrl() {
        return "https://" + randomWord(8) + ".atlassian.net";
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String randomRoleName() {
        return "Senior QA Analyst";
    }

    public static String randomDescription() {
        return "You are a Senior QA Automation Engineer with 10+ years of experience in US loan servicing applications — including origination, servicing, claims, payments, insurance, and borrower management platforms.\n" +
                "Your task is to generate automation-ready test cases from the given Test Scenario. These test cases are directly consumed by an automated C# Playwright code generator.\n";
    }

    private static String randomWord() {
        return UUID.randomUUID()
                .toString()
                .replaceAll("[^a-zA-Z]", "")
                .substring(0, 8);
    }
}
