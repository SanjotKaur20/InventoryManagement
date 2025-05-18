package com.sanjot.inventory.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRequestStatusEmail(String toEmail, String itemName, String status, String remarks) {
        String subject = "Inventory Request - " + status;
        String message = "Hello,\n\n" +
                "We are writing to inform you that your inventory request for the item \"" + itemName + "\" has been processed.\n\n" +
                "Status: " + status + "\n" +
                "Remarks: " + (remarks != null && !remarks.isEmpty() ? remarks : "No additional notes provided.") + "\n\n" +
                "If you have any questions, feel free to contact the inventory team.\n\n" +
                "Best regards,\n" +
                "Inventory Management System";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Your OTP Code");
		message.setText("Your OTP code is:" + otp);

		mailSender.send(message);
	}

}
