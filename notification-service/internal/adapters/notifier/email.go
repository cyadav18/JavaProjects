package notifier

import (
	"log"
	"notification-service/internal/domain"
)

func (n *NotificationSender) SendEmail(user domain.UserResponse, subject, body string) error {
	// Simulate sending Email
	log.Printf(" Sending Email to %s (%s):\nSubject: %s\nBody: %s\n", user.Name, user.Email, subject, body)
	// Replace this with actual email logic (e.g. SMTP or Mailgun API)
	return nil
}
