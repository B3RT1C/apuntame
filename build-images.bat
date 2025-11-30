@echo off
REM Script para construir las imágenes Docker localmente
REM Ejecutar desde la raíz del proyecto: c:\home\apuntame\

echo ========================================
echo  Construyendo imagenes Docker
echo ========================================
echo.

echo [1/2] Construyendo imagen del BACKEND...
docker build -t ghcr.io/b3rt1c/apuntame-backend:latest ./backend
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo al construir la imagen del backend
    pause
    exit /b 1
)
echo ✓ Backend construido exitosamente
echo.

echo [2/2] Construyendo imagen del FRONTEND...
docker build -t ghcr.io/b3rt1c/apuntame-frontend:latest ./frontend
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo al construir la imagen del frontend
    pause
    exit /b 1
)
echo ✓ Frontend construido exitosamente
echo.

echo ========================================
echo  IMAGENES CONSTRUIDAS EXITOSAMENTE
echo ========================================
echo.
echo Imagenes creadas:
echo   - ghcr.io/b3rt1c/apuntame-backend:latest
echo   - ghcr.io/b3rt1c/apuntame-frontend:latest
echo.
echo Ubicacion de las imagenes en Docker:
docker inspect ghcr.io/b3rt1c/apuntame-backend:latest --format="Backend: {{.GraphDriver.Data.dir}}"
docker inspect ghcr.io/b3rt1c/apuntame-frontend:latest --format="Frontend: {{.GraphDriver.Data.dir}}"
echo.
echo Lista de todas las imagenes Docker locales:
docker images
echo.
echo Para probar localmente, ejecuta:
echo   docker compose up -d
echo.
echo Si todo funciona correctamente, ejecuta:
echo   push-images.bat
echo.
pause
