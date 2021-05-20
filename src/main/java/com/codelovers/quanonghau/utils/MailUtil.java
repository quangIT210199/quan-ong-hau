package com.codelovers.quanonghau.utils;

import com.codelovers.quanonghau.contrants.Contrants;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class MailUtil {

    public  static String getSiteURL(HttpServletRequest request, String path) {
        String siteURL = request.getRequestURL().toString();
        System.out.println("get Servlet Path " + request.getServletPath());
        return siteURL.replace(path, "");
    }

    public static JavaMailSenderImpl prepareMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(Contrants.MAIL_HOST);
        mailSender.setPort(Contrants.MAIL_PORT);
        mailSender.setUsername(Contrants.MAIL_USERNAME);
        mailSender.setPassword(Contrants.MAIL_PASSWORD);

        // Set mail propertie
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", Contrants.SMTP_AUTH);
        properties.setProperty("mail.smtp.starttls.enable", Contrants.SMTP_SECURED);

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
