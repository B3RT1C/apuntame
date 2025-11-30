# Apuntame - Sistema de Gestión de Restaurantes

Sistema completo de gestión de pedidos para restaurantes con interfaz web moderna y tiempo real.

## Requisitos Previos

- **Docker** (versión 20.10 o superior)
- **Docker Compose** (versión 2.0 o superior)
- **Mínimo 2GB de RAM** disponible
- **Puertos libres**: 80 (frontend), 8080 (backend), 5432 (PostgreSQL)

### Instalación de Docker

#### Windows
1. Descargar [Docker Desktop para Windows](https://www.docker.com/products/docker-desktop/)
2. Ejecutar el instalador
3. Reiniciar el equipo si es necesario

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

#### macOS
1. Descargar [Docker Desktop para Mac](https://www.docker.com/products/docker-desktop/)
2. Ejecutar el instalador

## Instalación Rápida

### 1. Descargar archivos

Clona este repositorio o descarga los archivos necesarios:

```bash
git clone -b docker-release https://github.com/B3RT1C/apuntame.git
cd apuntame
```

O descarga manualmente:
- `docker-compose.yml`
- `.env`

### 2. Configurar credenciales

Edita el archivo `.env` y reemplaza los valores `CHANGE_ME`:

```bash
# Abre el archivo .env con tu editor preferido
notepad .env        # Windows
nano .env          # Linux/macOS
```

**Campos obligatorios a configurar:**

```env
# Usuario y contraseña de la base de datos PostgreSQL
POSTGRES_USER=tu_usuario_aqui
POSTGRES_PASSWORD=tu_contraseña_segura_aqui

# Credenciales del backend (deben coincidir con las de arriba)
DB_USER=tu_usuario_aqui
DB_PASSWORD=tu_contraseña_segura_aqui

# Cargar datos de demostración (true/false)
INITIALIZE_DEMO_DATA=false
```

**⚠️ IMPORTANTE**:
- `POSTGRES_USER` debe ser igual a `DB_USER`
- `POSTGRES_PASSWORD` debe ser igual a `DB_PASSWORD`
- Usa contraseñas seguras (mínimo 12 caracteres)

### 3. Iniciar la aplicación

```bash
docker compose up -d
```

Este comando:
- Descarga las imágenes de Docker (primera vez solamente)
- Crea los contenedores para PostgreSQL, Backend y Frontend
- Genera automáticamente un JWT secret único
- Inicia todos los servicios

### 4. Verificar estado

```bash
docker compose ps
```

Deberías ver 3 contenedores ejecutándose:
- `apuntame-db` (PostgreSQL)
- `apuntame-backend` (API REST)
- `apuntame-frontend` (Interfaz web)

### 5. Acceder a la aplicación

Abre tu navegador y visita:

**http://localhost**

**Credenciales por defecto:**
- Usuario: `admin`
- Contraseña: `admin123`

**⚠️ Cambia la contraseña del administrador** después del primer inicio de sesión.

## Datos de Demostración

Si configuraste `INITIALIZE_DEMO_DATA=true` en el archivo `.env`, la aplicación se iniciará con:

- **23 productos** de ejemplo (menú de restaurante español)
- **8 categorías**: Entrantes, Ensaladas, Carnes, Pescados, Postres, Bebidas, Cafés, Infusiones
- **6 secciones**: Barra, Plancha, Fríos, Horno, Postres, Freidora
- Precios realistas entre 2€ y 24€

Si prefieres empezar con una base de datos vacía, usa `INITIALIZE_DEMO_DATA=false`.

## Comandos Útiles

### Ver logs en tiempo real
```bash
docker compose logs -f
```

### Ver logs de un servicio específico
```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres
```

### Detener la aplicación
```bash
docker compose down
```

### Detener y eliminar datos (⚠️ CUIDADO: borra la base de datos)
```bash
docker compose down -v
```

### Reiniciar un servicio
```bash
docker compose restart backend
```

### Actualizar a la última versión
```bash
docker compose pull
docker compose up -d
```

## Acceso desde Otros Dispositivos

Para acceder desde otros dispositivos en la misma red local:

1. Encuentra la IP de tu servidor:
   ```bash
   # Windows
   ipconfig

   # Linux/macOS
   ip addr show
   ```

2. Desde otro dispositivo, accede a:
   ```
   http://IP_DEL_SERVIDOR
   ```

   Ejemplo: `http://192.168.1.100`

**Nota**: Asegúrate de que el firewall permita conexiones al puerto 80.

## Estructura de Puertos

| Servicio   | Puerto Interno | Puerto Externo | Acceso        |
|------------|----------------|----------------|---------------|
| Frontend   | 80             | 80             | http://localhost |
| Backend    | 8080           | 8080           | http://localhost:8080 |
| PostgreSQL | 5432           | No expuesto    | Solo contenedores |

## Volúmenes de Datos

Los datos se almacenan en volúmenes Docker persistentes:

- `postgres_data`: Base de datos PostgreSQL
- `jwt_secrets`: Secret JWT auto-generado

**Los datos persisten** incluso si detienes los contenedores con `docker compose down`.

Para hacer **backup de la base de datos**:

```bash
docker exec apuntame-db pg_dump -U tu_usuario apuntame > backup.sql
```

Para **restaurar desde backup**:

```bash
docker exec -i apuntame-db psql -U tu_usuario apuntame < backup.sql
```

## Solución de Problemas

### Error: "puerto ya en uso"

Si el puerto 80 u 8080 ya está ocupado:

1. Edita `docker-compose.yml`
2. Cambia el mapeo de puertos:
   ```yaml
   ports:
     - "8080:80"  # Cambiar puerto externo a 8080
   ```

### Los contenedores no inician

Verifica los logs:
```bash
docker compose logs
```

Revisa que los puertos estén libres:
```bash
# Windows
netstat -ano | findstr ":80"

# Linux/macOS
sudo lsof -i :80
```

### Error de conexión a la base de datos

1. Verifica que las credenciales en `.env` sean correctas
2. Asegúrate de que PostgreSQL esté saludable:
   ```bash
   docker compose ps
   ```
3. Reinicia los servicios:
   ```bash
   docker compose restart
   ```

### Olvidé la contraseña del administrador

1. Detén los contenedores: `docker compose down`
2. Elimina los volúmenes: `docker volume rm apuntame_postgres_data`
3. Inicia de nuevo: `docker compose up -d`
4. Se creará un nuevo usuario admin con contraseña `admin123`

## Arquitectura del Sistema

```
┌─────────────┐
│   Navegador │ ← Usuario accede por http://localhost
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Frontend  │ ← Angular + Nginx (Puerto 80)
│  (Nginx)    │
└──────┬──────┘
       │ HTTP/WebSocket
       ▼
┌─────────────┐
│   Backend   │ ← Spring Boot (Puerto 8080)
│ (Spring)    │   - API REST
└──────┬──────┘   - WebSocket
       │           - JWT Auth
       ▼
┌─────────────┐
│  PostgreSQL │ ← Base de datos (Puerto 5432)
│  (Database) │
└─────────────┘
```

## Características

- ✅ **Gestión de pedidos en tiempo real** con WebSockets
- ✅ **Multi-usuario** con sistema de autenticación JWT
- ✅ **Interfaz responsive** adaptada a tablets y móviles
- ✅ **Gestión de inventario** (productos, categorías, secciones)
- ✅ **Estados de pedido**: preparación, entrega, pago
- ✅ **Temporizadores automáticos** para seguimiento de tiempo
- ✅ **Filtrado avanzado** de pedidos
- ✅ **Health checks** automáticos para todos los servicios
- ✅ **Auto-generación de JWT secret** único por instalación

## Seguridad

### Recomendaciones

1. **Cambia las contraseñas por defecto** inmediatamente
2. **Usa contraseñas seguras** en el archivo `.env`
3. **No compartas** el archivo `.env` con terceros
4. **Haz backups regulares** de la base de datos
5. **Mantén Docker actualizado**

### JWT Secret

El sistema genera automáticamente un JWT secret único en el primer inicio. Este secret:
- Se almacena en el volumen `jwt_secrets`
- Persiste entre reinicios
- Es único para cada instalación
- No necesita configuración manual

## Soporte

Para problemas, sugerencias o consultas:

- **GitHub Issues**: https://github.com/B3RT1C/apuntame/issues
- **Documentación**: https://github.com/B3RT1C/apuntame

## Licencia

Este proyecto está bajo licencia MIT. Ver archivo `LICENSE` para más detalles.

---

**Desarrollado con ❤️ para restaurantes**
