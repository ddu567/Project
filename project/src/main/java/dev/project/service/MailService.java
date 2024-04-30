package dev.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;
    private static final String senderEmail = "kkomisu567@gmail.com";
    private static int number;

    public static void createNumber() {
        number = (int)(Math.random() * 900000) + 100000; // 정확한 범위를 위한 수정
    }

    public MimeMessage createMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "<h3>요청하신 인증 번호입니다.</h3>" +
                    "<h1>" + number + "</h1>" +
                    "<h3>감사합니다.</h3>";
            message.setContent(body, "text/html; charset=UTF-8"); // 수정된 메소드
        } catch (MessagingException e) {
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }

        return message;
    }

    public int sendMail(String email) throws MessagingException {
        MimeMessage message = createMail(email);
        if (message != null) {
            javaMailSender.send(message);
            return number;
        } else {
            throw new MessagingException("이메일 생성 실패");
        }
    }
}
