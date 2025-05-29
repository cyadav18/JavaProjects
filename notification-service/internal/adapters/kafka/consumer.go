package kafka

import (
	"context"
	"encoding/json"
	"log"
	"notification-service/internal/app"
	"notification-service/internal/domain"
	"time"

	"github.com/segmentio/kafka-go"
)

type Consumer struct {
	Reader    *kafka.Reader
	App       *app.NotificationService
	TopicName string
}

func NewKafkaConsumers(k domain.Kafka, app *app.NotificationService) []*Consumer {
	consumers := make([]*Consumer, 0, len(k.Topic))

	for _, topic := range k.Topic {
		r := kafka.NewReader(kafka.ReaderConfig{
			Brokers:           k.BootstrapServer,
			Topic:             topic,
			GroupID:           k.GroupID,
			MinBytes:          10e3,
			MaxBytes:          10e6,
			SessionTimeout:    60 * time.Second,
			HeartbeatInterval: 10 * time.Second,
		})

		consumer := &Consumer{
			Reader:    r,
			App:       app,
			TopicName: topic,
		}

		consumers = append(consumers, consumer)
	}

	return consumers
}
func (c *Consumer) StartConsuming(ctx context.Context) {
	log.Printf(" Kafka Consumer started for topic: %s", c.TopicName)

	for {
		select {
		case <-ctx.Done():
			log.Printf(" Kafka consumer shutting down for topic: %s", c.TopicName)
			return
		default:
			m, err := c.Reader.ReadMessage(ctx)
			if err != nil {
				// Exit if context was cancelled
				if ctx.Err() != nil {
					log.Printf("Context cancelled for topic %s: %v", c.TopicName, ctx.Err())
					return
				}
				log.Printf(" Failed to read message from %s: %v", c.TopicName, err)
				time.Sleep(2 * time.Second)
				continue
			}

			var task domain.TaskMessage
			if err := json.Unmarshal(m.Value, &task); err != nil {
				log.Printf(" Failed to unmarshal message on topic %s: %v", c.TopicName, err)
				continue
			}

			log.Printf(" Received task event from topic %s: %+v", c.TopicName, task)

			if err := c.App.NotifyWatchers(task); err != nil {
				log.Printf(" Notification failed for topic %s: %v", c.TopicName, err)
			}
		}
	}
}

func (c *Consumer) Close() error {
	log.Printf(" Closing consumer for topic: %s", c.TopicName)
	return c.Reader.Close()
}
