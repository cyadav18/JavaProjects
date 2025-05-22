package main

import (
	"context"
	"log"
	"notification-service/config"
	"notification-service/internal/adapters/kafka"
	"notification-service/internal/adapters/notifier"
	"notification-service/internal/adapters/userfetcher"
	"notification-service/internal/app"
	"notification-service/internal/domain"
	"os"
	"os/signal"
	"syscall"
)

func main() {
	log.Println("[BOOT] Loading application config from config/config.json")

	configPath := os.Getenv("CONFIG_PATH")
	if configPath == "" {
		configPath = "config.json"
	}
	appCfg := config.LoadConfig(configPath)

	log.Println("[BOOT] Setting up dependencies")
	userFetcher := userfetcher.NewRPCUserFetcher(appCfg.LoginService.URL)
	whatsAppSender := notifier.NewNotificationSender(appCfg)
	notificationApp := app.NewNotificationService(userFetcher, whatsAppSender)

	log.Printf("[BOOT] Creating Kafka consumers for topics: %v\n", appCfg.Kafka.Topic)
	consumers := kafka.NewKafkaConsumers(domain.Kafka{
		BootstrapServer: appCfg.Kafka.BootstrapServer,
		Topic:           appCfg.Kafka.Topic,
		GroupID:         appCfg.Kafka.GroupID}, notificationApp)

	log.Println("[BOOT] Starting consumers...")
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)

	// Shutdown listener
	go func() {
		sig := <-sigChan
		log.Printf("[SHUTDOWN] Signal received: %v. Initiating shutdown...\n", sig)
		cancel()
	}()

	for _, c := range consumers {
		go c.StartConsuming(ctx)
	}

	log.Println("[SYSTEM] Notification Service is running. Press Ctrl+C to exit.")
	<-ctx.Done()
	log.Println("[SYSTEM] All Kafka consumers stopped. Service shutting down.")
}
