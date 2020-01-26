package com.dmgburg

import com.dmgburg.book.mail.MailingService
import org.junit.platform.commons.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class TestMailingService : MailingService {
    companion object {
        val log = LoggerFactory.getLogger(TestMailingService::class.java)
        val cofirmationMessages = ConcurrentHashMap<String, String>()
    }

    init {
        log.info({"Starting test mailing service"})
    }

    override fun sendMessage(msg: String, subj: String, to: String) {
        TODO()
    }

    override fun sendEmailConfirmation(to: String, hash: String) {
        cofirmationMessages[to] = hash
    }
}