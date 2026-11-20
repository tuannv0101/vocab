package com.example.demo.util;

import com.example.demo.entity.VocabWord;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParserUtil {

    private static final Pattern MULTI_POS_SEMI = Pattern.compile(
            "^\\s*\\(([a-zA-Z /,-]+)\\)\\s*;\\s*\\(([a-zA-Z /,-]+)\\)\\s*(.*)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LEADING_POS = Pattern.compile(
            "^\\s*\\(([a-zA-Z /,-]+)\\)\\s*(.*)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern KNOWN_POS_TOKEN = Pattern.compile(
            "^(v|n|adj|adv|phr|prep|conj|pron|pro|interj|idiom|det|phrase|phr\\s*v|n/v|v/n|adj/adv|v/adj|n/adj|pro/adv|adv/adj/n|v,\\s*n|n,\\s*v)$",
            Pattern.CASE_INSENSITIVE
    );

    public static class CleanedPosMeaning {
        private final String partOfSpeech;
        private final String meaning;

        public CleanedPosMeaning(String partOfSpeech, String meaning) {
            this.partOfSpeech = partOfSpeech;
            this.meaning = meaning;
        }

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public String getMeaning() {
            return meaning;
        }
    }

    public static CleanedPosMeaning cleanPosAndMeaning(String rawPos, String rawMeaning) {
        String pos = rawPos != null ? rawPos.trim() : "";
        String meaning = rawMeaning != null ? rawMeaning.trim() : "";

        if (meaning.isEmpty()) {
            return new CleanedPosMeaning(pos, meaning);
        }

        // Case 1: Pattern like "(n); (v) người cộng sự; liên kết"
        Matcher semiMatcher = MULTI_POS_SEMI.matcher(meaning);
        if (semiMatcher.matches()) {
            String p1 = semiMatcher.group(1).trim().toLowerCase();
            String p2 = semiMatcher.group(2).trim().toLowerCase();
            pos = "(" + p1 + ", " + p2 + ")";
            meaning = semiMatcher.group(3).trim();
        } else {
            // Case 2: Leading POS like "(n) những nhu cầu" or "(v) sơn, vẽ"
            Matcher leadingMatcher = LEADING_POS.matcher(meaning);
            if (leadingMatcher.matches()) {
                String candidate = leadingMatcher.group(1).trim().toLowerCase();
                if (KNOWN_POS_TOKEN.matcher(candidate).matches()) {
                    if (pos.isEmpty() || pos.equalsIgnoreCase("(" + candidate + ")")) {
                        pos = "(" + candidate + ")";
                    }
                    meaning = leadingMatcher.group(2).trim();
                }
            }
        }

        // Case 3: Inner POS tags in multi-meaning strings, e.g. "đỗ xe; (n) công viên"
        if (meaning.matches("(?i).*?;\\s*\\((v|n|adj|adv|phr)\\).*")) {
            Set<String> collectedPos = new LinkedHashSet<>();
            if (!pos.isEmpty()) {
                String clean = pos.replaceAll("[()]", "");
                for (String part : clean.split(",")) {
                    if (!part.isBlank()) collectedPos.add(part.trim().toLowerCase());
                }
            }
            Matcher innerMatcher = Pattern.compile("(?i)\\((v|n|adj|adv|phr)\\)").matcher(meaning);
            while (innerMatcher.find()) {
                collectedPos.add(innerMatcher.group(1).trim().toLowerCase());
            }
            if (!collectedPos.isEmpty()) {
                pos = "(" + String.join(", ", collectedPos) + ")";
            }
            // Strip inner POS tags from meaning: "; (n) " -> "; "
            meaning = meaning.replaceAll("(?i);\\s*\\((v|n|adj|adv|phr)\\)\\s*", "; ");
            // Strip any remaining leading POS
            meaning = meaning.replaceAll("(?i)^\\s*\\((v|n|adj|adv|phr)\\)\\s*", "");
        }

        return new CleanedPosMeaning(pos, meaning.trim());
    }

    public static List<VocabWord> parseRawText(String rawText) {
        List<VocabWord> words = new ArrayList<>();
        if (rawText == null || rawText.isBlank()) {
            return words;
        }

        String[] lines = rawText.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) {
                continue;
            }

            VocabWord word = parseSingleLine(trimmed);
            if (word != null && !word.getWord().isBlank()) {
                words.add(word);
            }
        }

        return words;
    }

    public static VocabWord parseSingleLine(String line) {
        String word = "";
        String ipa = "";
        String partOfSpeech = "";
        String meaning = "";

        // Case 1: Tab-separated (user's primary format)
        if (line.contains("\t")) {
            String[] tokens = line.split("\\t+");
            if (tokens.length >= 3) {
                word = tokens[0].trim();
                ipa = tokens[1].trim();
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < tokens.length; i++) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(tokens[i].trim());
                }
                meaning = sb.toString();
            } else if (tokens.length == 2) {
                word = tokens[0].trim();
                String second = tokens[1].trim();
                if (second.startsWith("/") && second.contains("/")) {
                    int lastSlash = second.lastIndexOf('/');
                    if (lastSlash < second.length() - 1) {
                        ipa = second.substring(0, lastSlash + 1).trim();
                        meaning = second.substring(lastSlash + 1).trim();
                    } else {
                        ipa = second;
                        meaning = "";
                    }
                } else {
                    meaning = second;
                }
            }
        } else {
            // Case 2: Not tab-separated. Look for slash phonetics /.../
            int firstSlash = line.indexOf('/');
            int lastSlash = line.lastIndexOf('/');
            if (firstSlash != -1 && lastSlash > firstSlash) {
                word = line.substring(0, firstSlash).trim();
                if (word.endsWith("-")) {
                    word = word.substring(0, word.length() - 1).trim();
                }
                ipa = line.substring(firstSlash, lastSlash + 1).trim();
                if (lastSlash < line.length() - 1) {
                    meaning = line.substring(lastSlash + 1).trim();
                    if (meaning.startsWith("-")) {
                        meaning = meaning.substring(1).trim();
                    }
                }
            } else if (line.contains(" - ")) {
                String[] parts = line.split(" - ", 2);
                word = parts[0].trim();
                meaning = parts[1].trim();
            } else if (line.contains(" : ") || line.contains(":")) {
                String[] parts = line.split(":", 2);
                word = parts[0].trim();
                meaning = parts[1].trim();
            } else {
                word = line;
                meaning = "";
            }
        }

        CleanedPosMeaning cleaned = cleanPosAndMeaning(partOfSpeech, meaning);
        word = word.trim();
        ipa = ipa.trim();
        partOfSpeech = cleaned.getPartOfSpeech();
        meaning = cleaned.getMeaning();

        return new VocabWord(word, ipa, partOfSpeech, meaning);
    }
}
