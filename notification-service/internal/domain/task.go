package domain

type TaskMessage struct {
	ID          string   `json:"id"`
	Subject     string   `json:"subject"`
	Description string   `json:"description"`
	Assignee    string   `json:"assignee"`
	CreatedBy   string   `json:"createdBy"`
	Watchers    []string `json:"watchers"`
}
