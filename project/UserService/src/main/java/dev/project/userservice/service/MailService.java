package dev.project.userservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final String senderEmail = "kkomisu567@gmail.com"; // 발신자 이메일 주소
    private static int number; // 인증 번호
    @Autowired
    private JavaMailSender javaMailSender; // Spring의 JavaMailSender 인터페이스를 사용하여 이메일 전송

    // 인증 번호 생성 메소드
    public static void createNumber() {
        number = (int) (Math.random() * 900000) + 100000; // 100000에서 999999 사이의 수
    }

    // 인증 이메일 생성 메소드
    public MimeMessage createMail(String mail) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "<h3>요청하신 인증 번호입니다.</h3>" +
                    "<h1>" + number + "</h1>" +
                    "<h3>감사합니다.</h3>";
            message.setContent(body, "text/html; charset=UTF-8");
        } catch (MessagingException e) {
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }

        return message;
    }

    // 이메일 전송 메소드
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
