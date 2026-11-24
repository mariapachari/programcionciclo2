@echo off
REM ==================================================================
REM  Sube el proyecto a GitHub como repositorio PRIVADO y agrega al
REM  docente "dmamanipar" como colaborador (requerimiento 0).
REM  Requisitos (una sola vez):
REM     winget install Git.Git
REM     winget install GitHub.cli
REM  Luego cierre y abra de nuevo esta ventana.
REM ==================================================================
setlocal
chcp 65001 >nul
cd /d "%~dp0"
set REPO=SysTransporte
set DOCENTE=dmamanipar

where git >nul 2>&1 || (echo Falta Git: winget install Git.Git & pause & exit /b 1)
where gh  >nul 2>&1 || (echo Falta GitHub CLI: winget install GitHub.cli & pause & exit /b 1)

echo [1/4] Iniciando sesion en GitHub (se abrira el navegador)...
gh auth status >nul 2>&1 || gh auth login --web --git-protocol https
if errorlevel 1 (echo No se pudo iniciar sesion & pause & exit /b 1)

for /f %%o in ('gh api user -q .login') do set OWNER=%%o
echo [2/4] Preparando el repositorio local...
if not exist .git git init -b main
git branch -M main
git config user.name >nul 2>&1 || git config user.name "%OWNER%"
git config user.email >nul 2>&1 || git config user.email "%OWNER%@users.noreply.github.com"
git add -A
git commit -m "SysTransporte v1.4.0" >nul 2>&1

echo [3/4] Creando el repositorio privado %REPO% y subiendo el codigo...
gh repo view %REPO% >nul 2>&1
if errorlevel 1 (
  gh repo create %REPO% --private --source . --remote origin --push
) else (
  git remote get-url origin >nul 2>&1 || for /f %%u in ('gh repo view %REPO% --json url -q .url') do git remote add origin %%u.git
  git push -u origin main
)
if errorlevel 1 (echo Error al subir el codigo & pause & exit /b 1)

echo [4/4] Invitando al docente %DOCENTE% como colaborador...
gh api -X PUT repos/%OWNER%/%REPO%/collaborators/%DOCENTE% -f permission=push >nul
if errorlevel 1 (echo No se pudo invitar a %DOCENTE%. Hagalo en Settings ^> Collaborators. & pause & exit /b 1)

echo.
echo  LISTO: https://github.com/%OWNER%/%REPO%
echo  El docente %DOCENTE% recibira la invitacion por correo.
echo  En la pestana "Actions" de GitHub se generara solo el instalador de Windows.
start https://github.com/%OWNER%/%REPO%
pause
