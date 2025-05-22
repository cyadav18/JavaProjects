package domain

type UserResponse struct {
	ID          string   `json:"id"`
	Name        string   `json:"username"`
	Email       string   `json:"email"`
	PhoneNumber string   `json:"phoneNumber"`
	Roles       []string `json:"roles"`
}
