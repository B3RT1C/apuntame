@echo off
REM Script para subir las imágenes Docker a GitHub Container Registry
REM Ejecutar SOLO después de probar que las imágenes funcionan correctamente

echo ========================================
echo  Subiendo imagenes a GitHub Container Registry
echo ========================================
echo.

echo ADVERTENCIA: Esto publicara las imagenes para que los clientes las descarguen.
echo Asegurate de haber probado las imagenes localmente antes de continuar.
echo.
set /p confirm="¿Estas seguro de que quieres continuar? (S/N): "
if /i not "%confirm%"=="S" (
    echo.
    echo Operacion cancelada.
    pause
    exit /b 0
)
echo.

echo Verificando autenticacion en GitHub Container Registry...
echo Si no estas autenticado, se te pedira tu Personal Access Token.
echo.
docker login ghcr.io -u b3rt1c
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo el login. Verifica tus credenciales.
    echo Necesitas un Personal Access Token con permisos write:packages
    echo Crealo en: https://github.com/settings/tokens
    pause
    exit /b 1
)
echo.
echo ✓ Autenticacion exitosa
echo.

echo [1/2] Subiendo imagen del BACKEND...
docker push ghcr.io/b3rt1c/apuntame-backend:latest
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo al subir la imagen del backend
    pause
    exit /b 1
)
echo ✓ Backend subido exitosamente
echo.

echo [2/2] Subiendo imagen del FRONTEND...
docker push ghcr.io/b3rt1c/apuntame-frontend:latest
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo al subir la imagen del frontend
    pause
    exit /b 1
)
echo ✓ Frontend subido exitosamente
echo.

echo ========================================
echo  IMAGENES PUBLICADAS EXITOSAMENTE
echo ========================================
echo.
echo Las imagenes ahora estan disponibles en:
echo   - ghcr.io/b3rt1c/apuntame-backend:latest
echo   - ghcr.io/b3rt1c/apuntame-frontend:latest
echo.
echo Los clientes pueden actualizar ejecutando:
echo   docker compose pull
echo   docker compose up -d
echo.
pause
