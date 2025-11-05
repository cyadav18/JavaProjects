package main

import (
	"filer-server/config"
	"filer-server/handlers"
	"net/http"
)

type Route struct {
	AppConfig *config.AppConfig
}

func GetRoute(cfg *config.AppConfig) Route {
	return Route{
		AppConfig: cfg,
	}
}

func (r *Route) SetupRoutes() {
	http.HandleFunc("/upload", handlers.UploadHandler(r.AppConfig.UploadDir))
	http.HandleFunc("/files/", handlers.DownloadHandler(r.AppConfig.UploadDir))
}
