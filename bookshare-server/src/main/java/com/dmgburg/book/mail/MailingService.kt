package com.dmgburg.book.mail

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.UriUtils
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

interface MailingService {
    fun sendMessage(msg: String, subj: String, to: String)
    fun sendEmailConfirmation(to: String, hash: String)
}

@Component
class MailingServiceImpl(@Value("\${dmgburg.mail.baseUrl}") val baseUrl: String,
                         @Value("\${dmgburg.mail.host}") val serverHost: String,
                         @Value("\${dmgburg.mail.port}") val serverPort: Int,
                         @Value("\${dmgburg.mail.username}") val username: String,
                         @Value("\${dmgburg.mail.password}") val password: String?) : MailingService {
    private val session: Session
    private val baseURL: String
    override fun sendMessage(msg: String, subj: String, to: String) {
        try {
            val message: Message = MimeMessage(session)
            message.setFrom(InternetAddress(" support@co-books.com"))
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to))
            message.subject = subj
            val mimeBodyPart = MimeBodyPart()
            mimeBodyPart.setContent(msg, "text/html")
            val multipart: Multipart = MimeMultipart()
            multipart.addBodyPart(mimeBodyPart)
            message.setContent(multipart)
            Transport.send(message)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun sendEmailConfirmation(to: String, hash: String) {
        val encoded = UriUtils.encode(to,"UTF-8")
        var text = "Please <a href=\"https://$baseUrl/api/user/public/confirmUser/${encoded}/$hash\">confirm</a> your email at co-books.com"
        sendMessage(text, "Please confirm your account at co-boocs.com", to)
    }

    fun sendBookReqest(holder: String, requestor: String, name: String, id: Long) {
        val url = Resources.getResource("foo.txt")
        var text = Resources.toString(url, Charsets.UTF_8)
        text = text.replace("\${requestor}", requestor)
        text = text.replace("\${bookName}", name)
        text = text.replace("\${baseURL}", baseURL)
        text = text.replace("\${requestId}", baseURL)
        sendMessage(text, "", holder)
    }

    init {
        val prop = Properties()
        prop.setProperty("mail.smtp.auth", "true")
        prop.setProperty("mail.smtp.host", serverHost)
        prop.setProperty("mail.smtp.port", serverPort.toString())
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        session = Session.getInstance(prop, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })
        baseURL = baseUrl
    }
}