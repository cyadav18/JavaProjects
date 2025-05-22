package ports

import "notification-service/internal/domain"

type Notifier interface {
	SendWhatsApp(user domain.UserResponse, message string) error
	SendEmail(user domain.UserResponse, subject, message string) error
}
