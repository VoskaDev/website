package com.voska.website.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class SlugUtil {

    public String generate(String text) {
        if (text == null || text.isBlank()){
            throw new IllegalArgumentException("Slug oluşturulacak metin boş olamaz");
        }

        String normalized = text.toLowerCase(Locale.forLanguageTag("tr-TR"))
                .replace("ı","i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ö", "o")
                .replace("ç", "c");

        normalized = Normalizer.normalize(normalized,Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

}
