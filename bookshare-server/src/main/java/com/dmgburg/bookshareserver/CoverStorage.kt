package com.dmgburg.bookshareserver

import com.dmgburg.bookshareserver.domain.Cover
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Component
class CoverStorage(@Value("\${dmgburg.coversRoot:.}") val rootFolder: String) {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val STRING_LENGTH: Long = 20L

    fun loadCover(coverId: String): ByteArray {
        val file = file(coverId)
        return if (file.exists()) file.readBytes() else ByteArray(0)
    }

    fun saveCover(data: ByteArray): String {
        var coverFile = file(generateName())
        while (coverFile.exists()) {
            coverFile = file(generateName())
        }
        coverFile.writeBytes(data)
        return coverFile.name
    }

    private fun generateName() = ThreadLocalRandom.current()
            .ints(STRING_LENGTH.toLong(), 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("")

    private fun file(coverId: String?) = File("$rootFolder/$coverId")
}
