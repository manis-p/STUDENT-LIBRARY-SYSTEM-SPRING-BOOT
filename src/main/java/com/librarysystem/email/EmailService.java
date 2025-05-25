package com.librarysystem.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendOtpEmail(String toEmail, String userName, String otp,String resetLink) {
		try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true); 
	        helper.setTo(toEmail);
	        helper.setFrom("jham7340@gmail.com");
	        helper.setSubject("Password Reset Request - Student Library System");

	        String htmlContent = "<p>Dear <strong>" + userName + "</strong>,</p>"
	                + "<p>We received a request to reset your password for your <strong>Student Library System</strong> account.</p>"
	                + "<p>Please click the link below to reset your password:</p>"
	                + "<p><a href=\"" + resetLink + "\" style='color: blue; font-size: 16px;'>Reset Your Password</a></p>"
	                + "<p>This link is valid for <strong>5 minutes</strong>.</p>"
	                + "<p>If you did not request this, you can safely ignore this email.</p>"
	                + "<br/><p>Thank you,<br/>Student Library System Team</p>";

	        helper.setText(htmlContent, true); // true = HTML format

	        mailSender.send(message);

	    } catch (MessagingException e) {
	        e.printStackTrace(); 
	    }
	}

}
