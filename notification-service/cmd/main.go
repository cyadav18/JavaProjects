package main

import (
	"context"
	"io"
	"log"
	"notification-service/config"
	"notification-service/internal/adapters/kafka"
	"notification-service/internal/adapters/notifier"
	"notification-service/internal/adapters/userfetcher"
	"notification-service/internal/app"
	"notification-service/internal/domain"
	"os"
	"os/signal"
	"path/filepath"
	"syscall"
)

func main() {
	log.Printf("[BOOT] Loading application config from %s", os.Getenv("CONFIG_PATH"))
	configPath := os.Getenv("CONFIG_PATH")
	if configPath == "" {
		configPath = "config/config.json"
	}
	appCfg := config.LoadConfig(configPath)
	// Set default log file path if not in config
	logFilePath := appCfg.LogFilePath
	if logFilePath == "" {
		logFilePath = "logs/notification-service.log"
	}

	// Create directory if not exists
	logDir := filepath.Dir(logFilePath)
	if err := os.MkdirAll(logDir, 0755); err != nil {
		log.Fatalf("[FATAL] Failed to create log directory: %v", err)
	}

	// Open log file
	logFile, err := os.OpenFile(logFilePath, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0644)
	if err != nil {
		log.Fatalf("[FATAL] Failed to open log file: %v", err)
	}

	// Optional: log to both stdout and file
	log.SetOutput(io.MultiWriter(os.Stdout, logFile))
	log.SetFlags(log.LstdFlags | log.Lshortfile)

	log.Printf("[BOOT] Logging initialized. Writing to %s", logFilePath)

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
