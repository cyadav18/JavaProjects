package ports

import "notification-service/internal/domain"

type UserFetcher interface {
	GetUserByID(id string) (*domain.UserResponse, error)
}
