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

	public void sendOtpEmail(String toEmail, String userName, String otp, String resetLink, String purpose) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setFrom("jham7340@gmail.com");

			String htmlContent;

			if ("LOGIN".equals(purpose)) {
				helper.setSubject("Login OTP - Student Library System");
				htmlContent = "<p>Hi <strong>" + userName + "</strong>,</p>"
						+ "<p>Your login OTP is: <strong style='color:green; font-size:18px;'>" + otp + "</strong></p>"
						+ "<p>This OTP is valid for <strong>5 minutes</strong>.</p>";
			} else { // password reset
				helper.setSubject("Password Reset Request - Student Library System");
				htmlContent = "<p>Dear <strong>" + userName + "</strong>,</p>"
						+ "<p>We received a request to reset your password.</p>";

				if (otp != null) {
					htmlContent += "<p>Your OTP is: <strong style='color:green; font-size:18px;'>" + otp
							+ "</strong></p>";
				}

				htmlContent += "<p>Click below to reset your password:</p>"
						+ "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
						+ "<p>This link is valid for <strong>5 minutes</strong>.</p>";
			}

			htmlContent += "<br/><p>Thanks,<br/>Student Library System Team</p>";

			helper.setText(htmlContent, true);
			mailSender.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
