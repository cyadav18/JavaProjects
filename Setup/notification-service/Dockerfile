# Stage 1: Build the Go binary
FROM golang:1.24 AS builder
LABEL authors="cyadav"

WORKDIR /build-notification-service

# ✅ Fix for TLS/cert issues + Go proxy + checksum DB
RUN apt-get update && apt-get install -y ca-certificates
ENV GOINSECURE=golang.org/x
ENV GOPROXY=direct
ENV GOSUMDB=off

# Copy go.mod and go.sum first to leverage Docker cache
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest of the source code
COPY . .

# Build the binary
RUN go build -o notification-service main.go

# Stage 2: Create a small runtime image
FROM alpine:latest
WORKDIR /notification

# Copy the compiled binary from the builder
COPY --from=builder /build-notification-service/notification-service .

# Optional: expose port if needed
# EXPOSE 8083

# Run the app
ENTRYPOINT ["./notification-service"]
