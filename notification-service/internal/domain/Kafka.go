package domain

type Kafka struct {
	BootstrapServer []string `json:"bootstrapServer"`
	Topic           []string `json:"topic"`
	GroupID         string   `json:"groupId"`
}
