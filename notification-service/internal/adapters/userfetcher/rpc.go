package userfetcher

import (
	"encoding/json"
	"fmt"
	"net/http"
	"notification-service/internal/domain"
)

type RPCUserFetcher struct {
	LoginServiceBaseURL string
}

func NewRPCUserFetcher(baseURL string) *RPCUserFetcher {
	return &RPCUserFetcher{LoginServiceBaseURL: baseURL}
}

func (r *RPCUserFetcher) GetUserByID(id string) (*domain.UserResponse, error) {
	url := fmt.Sprintf("%s/api/auth/users/id/%s", r.LoginServiceBaseURL, id)

	resp, err := http.Get(url)
	if err != nil {
		return nil, fmt.Errorf("failed to call login service: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("login service returned non-200: %d", resp.StatusCode)
	}

	var user domain.UserResponse
	if err := json.NewDecoder(resp.Body).Decode(&user); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &user, nil
}
