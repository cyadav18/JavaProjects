package notifier

import (
	"fmt"
	"log"

	"notification-service/config"
	"notification-service/internal/domain"

	"github.com/twilio/twilio-go"
	api "github.com/twilio/twilio-go/rest/api/v2010"
)

type NotificationSender struct {
	cfg    config.AppConfig
	client *twilio.RestClient
}

func NewNotificationSender(cfg config.AppConfig) *NotificationSender {
	client := twilio.NewRestClientWithParams(twilio.ClientParams{
		Username: cfg.Twilio.AccountSID,
		Password: cfg.Twilio.AuthToken,
	})
	return &NotificationSender{cfg: cfg, client: client}
}

func (n *NotificationSender) SendWhatsApp(user domain.UserResponse, message string) error {
	// Simulate sending WhatsApp
	log.Printf(" Sending WhatsApp to %s (%s):\n%s\n", user.Name, user.PhoneNumber, message)
	// Replace this with actual WhatsApp API logic

	params := &api.CreateMessageParams{}
	params.SetTo("whatsapp:" + user.PhoneNumber)
	params.SetFrom("whatsapp:" + n.cfg.Twilio.WhatsAppNumber)
	params.SetBody(message)

	msg, err := n.client.Api.CreateMessage(params)
	if err != nil {
		return fmt.Errorf("failed to send WhatsApp message: %w", err)
	}

	if msg.Status == nil || *msg.Status != "queued" {
		return fmt.Errorf("message not queued successfully, status: %v, error: %v",
			safeStr(msg.Status), safeStr(msg.ErrorMessage))
	}

	fmt.Printf("WhatsApp message queued successfully. SID: %s, Status: %s",
		*msg.Sid, *msg.Status)
	return nil
}

func safeStr(ptr *string) string {
	if ptr == nil {
		return "nil"
	}
	return *ptr
}
