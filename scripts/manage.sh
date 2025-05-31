#!/bin/bash

# Management script for the Spring Boot application

SERVICE_NAME="hackathon-backend"
LOG_DIR="/var/log/$SERVICE_NAME"

case "$1" in
    start)
        echo "Starting $SERVICE_NAME..."
        sudo systemctl start $SERVICE_NAME
        echo "Service started."
        ;;
    stop)
        echo "Stopping $SERVICE_NAME..."
        sudo systemctl stop $SERVICE_NAME
        echo "Service stopped."
        ;;
    restart)
        echo "Restarting $SERVICE_NAME..."
        sudo systemctl restart $SERVICE_NAME
        echo "Service restarted."
        ;;
    status)
        echo "Service status:"
        sudo systemctl status $SERVICE_NAME --no-pager
        ;;
    logs)
        echo "Recent logs:"
        sudo journalctl -u $SERVICE_NAME --no-pager --lines=50
        ;;
    tail)
        echo "Tailing logs (Ctrl+C to exit):"
        sudo journalctl -u $SERVICE_NAME -f
        ;;
    health)
        echo "Checking application health..."
        curl -s http://localhost:8080/actuator/health | jq . || curl http://localhost:8080/actuator/health
        ;;
    enable)
        echo "Enabling $SERVICE_NAME to start on boot..."
        sudo systemctl enable $SERVICE_NAME
        echo "Service enabled."
        ;;
    disable)
        echo "Disabling $SERVICE_NAME from starting on boot..."
        sudo systemctl disable $SERVICE_NAME
        echo "Service disabled."
        ;;
    deploy)
        echo "Running deployment..."
        ./deploy.sh
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status|logs|tail|health|enable|disable|deploy}"
        echo ""
        echo "Commands:"
        echo "  start    - Start the service"
        echo "  stop     - Stop the service"
        echo "  restart  - Restart the service"
        echo "  status   - Show service status"
        echo "  logs     - Show recent logs"
        echo "  tail     - Follow logs in real-time"
        echo "  health   - Check application health endpoint"
        echo "  enable   - Enable service to start on boot"
        echo "  disable  - Disable service from starting on boot"
        echo "  deploy   - Run deployment script"
        exit 1
        ;;
esac 