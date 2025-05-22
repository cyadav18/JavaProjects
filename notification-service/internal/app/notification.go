package app

import (
	"fmt"
	"notification-service/internal/domain"
	"notification-service/internal/ports"
)

type NotificationService struct {
	UserFetcher ports.UserFetcher
	Notifier    ports.Notifier
}

func NewNotificationService(userFetcher ports.UserFetcher, notifier ports.Notifier) *NotificationService {
	return &NotificationService{
		UserFetcher: userFetcher,
		Notifier:    notifier,
	}
}

func (n *NotificationService) NotifyWatchers(task domain.TaskMessage) error {
	for _, watcherID := range task.Watchers {
		user, err := n.UserFetcher.GetUserByID(watcherID)
		if err != nil {
			return fmt.Errorf("failed to get user %s: %w", watcherID, err)
		}

		message := fmt.Sprintf(" Task: %s\n Description: %s", task.Subject, task.Description)
		if err := n.Notifier.SendWhatsApp(*user, message); err != nil {
			return fmt.Errorf("failed to send WhatsApp to %s: %w", user.Name, err)
		}

		// Optional: send email too
		//if err := n.Notifier.SendEmail(*user, task.Subject, task.Description); err != nil {
		//	return fmt.Errorf("failed to send email to %s: %w", user.Mail, err)
		//}
	}
	return nil
}
