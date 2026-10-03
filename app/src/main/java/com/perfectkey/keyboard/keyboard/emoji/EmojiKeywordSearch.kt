// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.keyboard.emoji

import android.content.Context
import java.text.Normalizer

/**
 * Emoji search by name and keywords (data from Emojibase, MIT license, in assets/emoji_search/emoji_<language>.tsv:
 * emoji, tab, name, tab, keywords). Words are matched exactly, by prefix and as part of a word, the name counts more
 * than the keywords, and every word of the query has to match.
 */
object EmojiKeywordSearch {
    private class Entry(val emoji: String, val nameWords: List<String>, val tagWords: List<String>, val order: Int)

    private val indexes = HashMap<String, List<Entry>>()

    private fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    private fun index(context: Context, language: String): List<Entry> = synchronized(indexes) {
        indexes.getOrPut(language) {
            val file = if (context.assets.list("emoji_search")?.contains("emoji_$language.tsv") == true) "emoji_$language.tsv" else "emoji_en.tsv"
            runCatching {
                context.assets.open("emoji_search/$file").bufferedReader().useLines { lines ->
                    lines.mapIndexedNotNull { i, line ->
                        val parts = line.split('\t')
                        if (parts.size < 2) null
                        else Entry(parts[0], normalize(parts[1]).split(' ').filter { it.isNotEmpty() },
                            normalize(parts.getOrElse(2) { "" }).split(' ').filter { it.isNotEmpty() }, i)
                    }.toList()
                }
            }.getOrDefault(emptyList())
        }
    }

    private fun scoreWord(token: String, words: List<String>, exact: Int, prefix: Int, inside: Int): Int {
        var best = 0
        for (w in words) {
            val s = when {
                w == token -> exact
                w.startsWith(token) -> prefix
                token.length >= 3 && w.contains(token) -> inside
                else -> 0
            }
            if (s > best) best = s
        }
        return best
    }

    /** Matching emojis, best first; empty if the query is blank or nothing matches. */
    fun search(query: String, language: String, context: Context, limit: Int = 60): List<String> {
        val tokens = normalize(query).split(' ').filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return emptyList()
        val scored = ArrayList<Pair<Int, Entry>>()
        for (entry in index(context, language)) {
            var total = 0
            var matchedAll = true
            for (t in tokens) {
                val s = maxOf(scoreWord(t, entry.nameWords, 100, 60, 25), scoreWord(t, entry.tagWords, 80, 45, 15))
                if (s == 0) { matchedAll = false; break }
                total += s
            }
            if (!matchedAll) continue
            // a name that is exactly the query, or shorter names, come first
            if (entry.nameWords.joinToString(" ") == tokens.joinToString(" ")) total += 40
            total -= entry.nameWords.size
            scored.add(total to entry)
        }
        return scored.sortedWith(compareByDescending<Pair<Int, Entry>> { it.first }.thenBy { it.second.order })
            .take(limit).map { it.second.emoji }
    }
}
