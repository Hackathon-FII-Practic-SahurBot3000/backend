# Deployment Guide

This guide explains how to deploy the Spring Boot application to a VPS without Docker.

## Prerequisites

- Ubuntu/Debian VPS with sudo access
- Java 21 (will be installed automatically by the deployment script)
- Maven (will be installed automatically by the deployment script)
- Git installed on the VPS

## Deployment Methods

### 1. Automatic Deployment via GitHub Actions

The application is automatically deployed when:
- A new release is published
- Manual trigger via GitHub Actions

The workflow will:
1. Connect to the VPS via SSH
2. Clone/update the repository
3. Build the application using Maven
4. Create a systemd service
5. Start the application

### 2. Manual Deployment

If you need to deploy manually:

```bash
# SSH into your VPS
ssh your-user@your-vps-ip

# Clone the repository (first time only)
sudo mkdir -p /opt/hackathon-backend
cd /opt/hackathon-backend
sudo git clone https://github.com/hackathon-fii-practic-sahurbot3000/backend.git .

# Make scripts executable
chmod +x scripts/deploy.sh
chmod +x scripts/manage.sh

# Run deployment
./scripts/deploy.sh
```

## Application Management

Use the management script for common operations:

```bash
cd /opt/hackathon-backend

# Start the service
./scripts/manage.sh start

# Stop the service
./scripts/manage.sh stop

# Restart the service
./scripts/manage.sh restart

# Check service status
./scripts/manage.sh status

# View recent logs
./scripts/manage.sh logs

# Follow logs in real-time
./scripts/manage.sh tail

# Check application health
./scripts/manage.sh health

# Enable service to start on boot
./scripts/manage.sh enable

# Disable service from starting on boot
./scripts/manage.sh disable
```

## Configuration

### Environment Variables

The application uses environment variables for configuration. These are set in the systemd service file:

- `SPRING_PROFILES_ACTIVE=prod` - Activates production profile
- `DATABASE_URL` - PostgreSQL database URL
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `JWT_SECRET` - Secret for JWT token signing
- `GOOGLE_CLIENT_ID` - Google OAuth client ID
- `GOOGLE_CLIENT_SECRET` - Google OAuth client secret
- `FRONTEND_URL` - Frontend application URL

### Production vs Development

The application automatically uses the `prod` profile in production, which:
- Uses conservative database settings (`ddl-auto=validate`)
- Disables SQL logging for better performance
- Optimizes connection pool settings
- Enables production logging levels

## Monitoring

### Health Check

The application exposes health endpoints:
- Health: `http://your-vps:8080/actuator/health`
- Metrics: `http://your-vps:8080/actuator/metrics`
- Info: `http://your-vps:8080/actuator/info`

### Logs

Application logs are stored in:
- Standard output: `/var/log/hackathon-backend/app.log`
- Errors: `/var/log/hackathon-backend/error.log`
- System logs: `journalctl -u hackathon-backend`

### Service Management

The application runs as a systemd service:

```bash
# Check service status
sudo systemctl status hackathon-backend

# View service logs
sudo journalctl -u hackathon-backend

# Follow service logs
sudo journalctl -u hackathon-backend -f
```

## Troubleshooting

### Common Issues

1. **Service won't start**
   ```bash
   # Check logs for errors
   sudo journalctl -u hackathon-backend --no-pager --lines=50
   
   # Check if Java is installed correctly
   java -version
   
   # Check if port 8080 is available
   sudo netstat -tlnp | grep 8080
   ```

2. **Database connection issues**
   ```bash
   # Test database connectivity
   telnet octavianregatun.com 5432
   
   # Check database environment variables
   sudo systemctl show hackathon-backend --property=Environment
   ```

3. **Application crashes**
   ```bash
   # Check memory usage
   free -h
   
   # Check disk space
   df -h
   
   # View error logs
   sudo tail -f /var/log/hackathon-backend/error.log
   ```

### Performance Tuning

To adjust JVM memory settings, edit the systemd service file:

```bash
sudo nano /etc/systemd/system/hackathon-backend.service
```

Modify the `JAVA_OPTS` environment variable:
```
Environment=JAVA_OPTS=-Xmx1g -Xms512m
```

Then reload and restart:
```bash
sudo systemctl daemon-reload
sudo systemctl restart hackathon-backend
```

## Security Considerations

- The application runs as a non-root user
- Sensitive configuration is managed via environment variables
- Database credentials should be secured
- Consider using a reverse proxy (nginx) for SSL termination
- Regular security updates should be applied to the VPS

## Backup and Recovery

### Database Backup

Create regular backups of your PostgreSQL database:

```bash
pg_dump -h octavianregatun.com -U postgres hackathon-fiipractic > backup.sql
```

### Application Backup

The application JAR and logs are stored in `/opt/hackathon-backend` and `/var/log/hackathon-backend`.

## Scaling

For horizontal scaling, consider:
- Load balancer in front of multiple application instances
- Shared database
- Session management (if using sessions)
- File upload storage (shared storage or cloud storage) 