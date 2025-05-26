# Stage 1: Build the Go binary
FROM golang:1.24 AS builder
WORKDIR /build-notification-service

ENV CGO_ENABLED=0 \
    GOOS=linux \
    GOARCH=amd64 \
    GOINSECURE=golang.org/x \
    GOPROXY=direct \
    GOSUMDB=off

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o notification-service ./cmd/

# Stage 2: Small runtime image
FROM alpine:latest
WORKDIR /notification

COPY --from=builder /build-notification-service/notification-service .
RUN chmod +x notification-service

ENTRYPOINT ["./notification-service"]
