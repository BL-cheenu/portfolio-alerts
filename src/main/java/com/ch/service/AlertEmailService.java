package com.ch.service;

import com.ch.rabbitmq.AlertEmailMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * US10 - Email Alert Service
 * Called by RabbitMQ consumer to send breach alert emails
 */
@Service
public class AlertEmailService {

    private static final Logger log = LoggerFactory.getLogger(AlertEmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ── Send threshold breach alert email ────────────────────────────────
    public void sendAlertEmail(AlertEmailMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(message.getEmail());
            helper.setSubject(buildSubject(message));
            helper.setText(buildEmailBody(message), true); // true = HTML

            mailSender.send(mimeMessage);
            log.info("Alert email sent to: {}, Stock: {}, Breach: {}",
                    message.getEmail(), message.getStockSymbol(), message.getBreachType());

        } catch (Exception e) {
            log.error("Failed to send alert email to {}: {}", message.getEmail(), e.getMessage());
            throw new RuntimeException("Email send failed: " + e.getMessage());
        }
    }

    // ── Email subject ────────────────────────────────────────────────────
    private String buildSubject(AlertEmailMessage msg) {
        String type = "UPPER_BREACHED".equals(msg.getBreachType()) ? "🔺 Upper" : "🔻 Lower";
        return type + " Alert Triggered: " + msg.getStockSymbol()
                + " crossed ₹" + msg.getAlertPrice();
    }

    // ── HTML Email body ──────────────────────────────────────────────────
    private String buildEmailBody(AlertEmailMessage msg) {
        String breachColor = "UPPER_BREACHED".equals(msg.getBreachType()) ? "#27ae60" : "#e74c3c";
        String breachLabel = "UPPER_BREACHED".equals(msg.getBreachType())
                ? "🔺 Upper Threshold Breached" : "🔻 Lower Threshold Breached";
        String glColor = msg.getGainLoss() >= 0 ? "#27ae60" : "#e74c3c";
        String glSign  = msg.getGainLoss() >= 0 ? "+" : "";

        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; background:#f5f5f5; padding:20px;">
                  <div style="max-width:600px; margin:auto; background:#fff; border-radius:8px; padding:30px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                    <h2 style="color:#2c3e50;">📈 Portfolio Alert — Portfolio Alerts App</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>A stock price threshold has been breached in your portfolio.</p>

                    <div style="background:%s; color:#fff; padding:12px 20px; border-radius:6px; margin:20px 0;">
                      <strong>%s</strong>
                    </div>

                    <table style="width:100%%; border-collapse:collapse; margin:20px 0;">
                      <tr style="background:#f8f9fa;">
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Stock</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6;">%s (%s)</td>
                      </tr>
                      <tr>
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Buy Price</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6;">₹%s</td>
                      </tr>
                      <tr style="background:#f8f9fa;">
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Current Price</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6; font-weight:bold;">₹%s</td>
                      </tr>
                      <tr>
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Alert Price</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6;">₹%s (%.1f%% threshold)</td>
                      </tr>
                      <tr style="background:#f8f9fa;">
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Quantity</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6;">%d shares</td>
                      </tr>
                      <tr>
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Total Invested</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6;">₹%s</td>
                      </tr>
                      <tr style="background:#f8f9fa;">
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Current Value</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6;">₹%s</td>
                      </tr>
                      <tr>
                        <td style="padding:10px; border:1px solid #dee2e6;"><strong>Gain / Loss</strong></td>
                        <td style="padding:10px; border:1px solid #dee2e6; color:%s; font-weight:bold;">
                          %s₹%s (%s%.2f%%)
                        </td>
                      </tr>
                    </table>

                    <p style="color:#7f8c8d; font-size:12px;">Triggered at: %s</p>
                    <p style="color:#7f8c8d; font-size:12px;">This is an automated alert from Portfolio Alerts App.</p>
                  </div>
                </body>
                </html>
                """.formatted(
                msg.getUsername(),
                breachColor, breachLabel,
                msg.getCompanyName(), msg.getStockSymbol(),
                msg.getBuyPrice(),
                msg.getCurrentPrice(),
                msg.getAlertPrice(), msg.getThresholdValue(),
                msg.getQuantity(),
                msg.getTotalInvested(),
                msg.getCurrentValue(),
                glColor, glSign, Math.abs(msg.getGainLoss()), glSign, msg.getGainLossPercent(),
                msg.getTriggeredAt()
        );
    }
}