package config

import (
	"encoding/json"
	"log"
	"os"
)

type TwilioConfig struct {
	AccountSID     string `json:"accountSID"`
	AuthToken      string `json:"authToken"`
	WhatsAppNumber string `json:"whatsAppNumber"`
}

type KafkaConfig struct {
	BootstrapServer []string `json:"bootstrapServer"`
	Topic           []string `json:"topics"`
	GroupID         string   `json:"groupId"`
	MinBites        int      `json:"minBites,default=10e3"`
	MaxBites        int      `json:"maxBites,default=10e3"`
}

type LoginServiceConfig struct {
	URL string `json:"url"`
}

type AppConfig struct {
	Twilio       TwilioConfig       `json:"twilio"`
	Kafka        KafkaConfig        `json:"kafka"`
	LoginService LoginServiceConfig `json:"loginService"`
}

func LoadConfig(path string) AppConfig {
	file, err := os.Open(path)
	if err != nil {
		log.Fatalf("Error opening config file: %v", err)
	}
	defer file.Close()

	var config AppConfig
	if err := json.NewDecoder(file).Decode(&config); err != nil {
		log.Fatalf("Error decoding config JSON: %v", err)
	}
	return config
}
