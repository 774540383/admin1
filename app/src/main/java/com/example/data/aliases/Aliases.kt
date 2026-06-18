package com.example.data.aliases

object TeamAliases {
    val enToAr = mapOf(
        "Real Madrid" to "ريال مدريد",
        "Barcelona" to "برشلونة",
        "Atletico Madrid" to "أتلتيكو مدريد",
        "Manchester United" to "مانشستر يونايتد",
        "Manchester City" to "مانشستر سيتي",
        "Liverpool" to "ليفربول",
        "Arsenal" to "أرسنال",
        "Chelsea" to "تشيلسي",
        "Tottenham" to "توتنهام",
        "Bayern Munich" to "بايرن ميونخ",
        "Borussia Dortmund" to "بوروسيا دورتموند",
        "Paris Saint-Germain" to "باريس سان جيرمان",
        "Juventus" to "يوفنتوس",
        "AC Milan" to "ميلان",
        "Inter Milan" to "إنتر ميلان",
        "Napoli" to "نابولي",
        "Al Hilal" to "الهلال",
        "Al Nassr" to "النصر",
        "Al Ittihad" to "الاتحاد",
        "Al Ahli" to "الأهلي السعودي",
        "Al Ahly" to "الأهلي المصري",
        "Zamalek" to "الزمالك",
        "Wydad" to "الوداد البيضاوي",
        "Raja" to "الرجاء البيضاوي",
        "Esperance" to "الترجي التونسي"
    )

    fun translate(name: String, toArabic: Boolean): String {
        return if (toArabic) {
            enToAr[name] ?: enToAr.entries.find { it.key.equals(name, ignoreCase = true) }?.value ?: name
        } else {
            enToAr.entries.find { it.value.equals(name, ignoreCase = true) }?.key ?: name
        }
    }
}

object LeagueAliases {
    val enToAr = mapOf(
        "Premier League" to "الدوري الإنجليزي الممتاز",
        "La Liga" to "الدوري الإسباني",
        "Serie A" to "الدوري الإيطالي",
        "Bundesliga" to "الدوري الألماني",
        "Ligue 1" to "الدوري الفرنسي",
        "Champions League" to "دوري أبطال أوروبا",
        "Europa League" to "الدوري الأوروبي",
        "Saudi Pro League" to "دوري روشن السعودي",
        "Egyptian Premier League" to "الدوري المصري الممتاز",
        "World Cup" to "كأس العالم",
        "Club World Cup" to "كأس العالم للأندية"
    )

    fun translate(name: String, toArabic: Boolean): String {
        return if (toArabic) {
            enToAr[name] ?: enToAr.entries.find { it.key.equals(name, ignoreCase = true) }?.value ?: name
        } else {
            enToAr.entries.find { it.value.equals(name, ignoreCase = true) }?.key ?: name
        }
    }
}

object CountryAliases {
    val enToAr = mapOf(
        "England" to "إنجلترا",
        "Spain" to "إسبانيا",
        "Italy" to "إيطاليا",
        "Germany" to "ألمانيا",
        "France" to "فرنسا",
        "Saudi Arabia" to "المملكة العربية السعودية",
        "Egypt" to "مصر",
        "Morocco" to "المغرب",
        "Tunisia" to "تونس",
        "Europe" to "أوروبا",
        "International" to "دوليات"
    )

    fun translate(name: String, toArabic: Boolean): String {
        return if (toArabic) {
            enToAr[name] ?: enToAr.entries.find { it.key.equals(name, ignoreCase = true) }?.value ?: name
        } else {
            enToAr.entries.find { it.value.equals(name, ignoreCase = true) }?.key ?: name
        }
    }
}

object ChannelAliases {
    val enToAr = mapOf(
        "beIN Sports 1" to "بي إن سبورتس 1",
        "beIN Sports 2" to "بي إن سبورتس 2",
        "beIN Sports Premium" to "بي إن سبورتس بريميوم",
        "SSC Sports 1" to "قناة SSC الرياضية 1",
        "SSC Sports Extra" to "قناة SSC الإضافية",
        "AD Sports" to "أبوظبي الرياضية",
        "KSA Sports" to "السعودية الرياضية",
        "Cairo 24" to "القاهرة 24 الرياضية",
        "OnTime Sports" to "أون تايم سبورتس"
    )

    fun translate(name: String, toArabic: Boolean): String {
        return if (toArabic) {
            enToAr[name] ?: enToAr.entries.find { it.key.equals(name, ignoreCase = true) }?.value ?: name
        } else {
            enToAr.entries.find { it.value.equals(name, ignoreCase = true) }?.key ?: name
        }
    }
}
