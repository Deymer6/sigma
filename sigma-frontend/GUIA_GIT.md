# GUIA_GIT.md - Guía de Git para Sigma Frontend

## Introducción a Git en Sigma

Esta guía te ayudará a dominar Git para trabajar en el proyecto Sigma Backend y Frontend.

## Tabla de Contenidos

1. [Configuración Inicial](#configuración-inicial)
2. [Conceptos Básicos](#conceptos-básicos)
3. [Flujo de Trabajo](#flujo-de-trabajo)
4. [Gestión de Ramas](#gestión-de-ramas)
5. [Commits Efectivos](#commits-efectivos)
6. [Merge vs Rebase](#merge-vs-rebase)
7. [Resolución de Conflictos](#resolución-de-conflictos)
8. [Comandos Útiles](#comandos-útiles)
9. [Troubleshooting](#troubleshooting)

---

## Configuración Inicial

### Configurar Identidad

```bash
# Nombre y email global
git config --global user.name "Tu Nombre"
git config --global user.email "tu.email@example.com"

# Verificar configuración
git config --global user.name
git config --global user.email

# Configuración local (para un proyecto)
git config user.name "Nombre Local"
git config user.email "local@example.com"
```

### Configurar Editor

```bash
# VSCode
git config --global core.editor "code --wait"

# Vim
git config --global core.editor "vim"

# Nano
git config --global core.editor "nano"
```

### Configuracion de Líneas

```bash
# Windows (CRLF)
git config --global core.autocrlf true

# Linux/Mac (LF)
git config --global core.autocrlf input
```

### Aliases Útiles

```bash
# Crear aliases
git config --global alias.st status
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.log 'log --oneline --graph --decorate --all'
git config --global alias.unstage 'restore --staged'
git config --global alias.last 'log -1 HEAD'
git config --global alias.visual 'log --graph --oneline --all'

# Usar aliases
git st          # git status
git co main     # git checkout main
git ci -m "msg" # git commit -m "msg"
```

---

## Conceptos Básicos

### Repositorio Local vs Remoto

```
┌─────────────────────────────────────────────────┐
│         TU MÁQUINA (Local)                      │
│  ┌──────────────────────────────────────────┐  │
│  │       Working Directory                  │  │
│  │   (Archivos en tu computadora)           │  │
│  └──────────────────────────────────────────┘  │
│            ↓ (git add)  ↑ (git checkout)      │
│  ┌──────────────────────────────────────────┐  │
│  │       Staging Area (Index)               │  │
│  │   (Cambios preparados para commit)       │  │
│  └──────────────────────────────────────────┘  │
│            ↓ (git commit)                      │
│  ┌──────────────────────────────────────────┐  │
│  │       Local Repository                   │  │
│  │   (.git directory)                       │  │
│  └──────────────────────────────────────────┘  │
│            ↕ (git push / git pull)             │
├─────────────────────────────────────────────────┤
│         SERVIDOR (Remote - GitHub)              │
│  ┌──────────────────────────────────────────┐  │
│  │       Remote Repository                  │  │
│  │   (origin, upstream, etc.)               │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### Estados de Archivos

```
Untracked → Tracked (git add)
  │           │
  └─→ Staged (git commit)
               │
            Committed
```

---

## Flujo de Trabajo

### Flujo Estándar

```bash
# 1. Ver estado del repositorio
git status

# 2. Ver cambios
git diff

# 3. Agregar cambios al staging
git add .                    # Todos los cambios
git add archivo.ts           # Archivo específico
git add src/app/             # Carpeta específica

# 4. Verificar staging
git diff --staged

# 5. Hacer commit
git commit -m "feat: agregar nueva funcionalidad"

# 6. Ver historial
git log --oneline

# 7. Enviar cambios al remoto
git push origin feature/nombre
```

### Crear Rama para Feature

```bash
# 1. Actualizar develop (branch principal)
git checkout develop
git pull origin develop

# 2. Crear rama de feature
git checkout -b feature/nueva-funcionalidad

# 3. Trabajar en feature...
# (hacer commits)

# 4. Enviar rama al remoto
git push -u origin feature/nueva-funcionalidad
```

---

## Gestión de Ramas

### Ver Ramas

```bash
# Ramas locales
git branch

# Ramas remotas
git branch -r

# Todas las ramas
git branch -a

# Ramas con commits activos
git branch -v
```

### Crear Ramas

```bash
# Crear desde rama actual
git checkout -b feature/nueva

# Crear desde rama específica
git checkout -b feature/nueva origin/develop

# Crear y configurar upstream
git push -u origin feature/nueva
```

### Cambiar Ramas

```bash
# Cambiar rama existente
git checkout develop

# Cambiar o crear rama (Git 2.23+)
git switch develop

# Volver a rama anterior
git checkout -
```

### Eliminar Ramas

```bash
# Local (solo si está merged)
git branch -d feature/antigua

# Forzar eliminación local
git branch -D feature/antigua

# Remoto
git push origin --delete feature/antigua

# Limpiar referencias remotas locales
git fetch origin --prune
```

### Renombrar Rama

```bash
# Renombrar localmente
git branch -m feature/antigua feature/nueva

# Actualizar en remoto
git push origin -u feature/nueva
git push origin --delete feature/antigua
```

---

## Commits Efectivos

### Hacer Commits

```bash
# Commit con mensaje en línea
git commit -m "feat: agregar validación"

# Commit con mensaje largo
git commit -m "feat: agregar validación de formulario

- Validar email formato
- Validar teléfono
- Mostrar mensajes de error"

# Commit interactivo (elige qué agregar)
git add -i  # o git add -p (patch mode)

# Amend (modificar último commit)
git commit --amend
git commit --amend --no-edit  # Sin cambiar mensaje
```

### Ver Commits

```bash
# Un commit por línea
git log --oneline

# Con gráfica de ramas
git log --graph --oneline --all

# Commits de cierto período
git log --since="2 weeks ago"
git log --until="2024-01-01"

# Por autor
git log --author="nombre"

# Commits que afectan un archivo
git log -- archivo.ts

# Ver cambios específicos
git show <hash>
git log -p archivo.ts
```

### Deshacer Commits

```bash
# Deshacer último commit (mantener cambios)
git reset --soft HEAD~1

# Deshacer último commit (sin cambios)
git reset --hard HEAD~1

# Deshacer commit del remoto
git revert <hash>  # Crear nuevo commit que deshace cambios

# Ver reflog (historial de referencias)
git reflog
```

---

## Merge vs Rebase

### Merge (Recomendado para PRs)

```bash
# En develop
git checkout develop
git pull origin develop

# Merge feature branch
git merge feature/nueva
# o
git merge --no-ff feature/nueva  # Crea commit de merge

# Si hay conflictos
git status  # Ver conflictos
# ... resolver manualmente ...
git add archivo-resuelto.ts
git commit -m "merge: resolver conflictos"
```

**Ventajas:**
- Preserva historial completo
- Fácil de entender todo lo que pasó

**Desventajas:**
- Historial puede ser confuso

### Rebase (Para limpiar historial local)

```bash
# En feature branch
git checkout feature/nueva
git rebase develop

# Si hay conflictos
# ... resolver ...
git add archivo.ts
git rebase --continue

# Abortar rebase
git rebase --abort
```

**Ventajas:**
- Historial lineal y limpio

**Desventajas:**
- Reescribe historial (⚠️ NO hacer en ramas compartidas)

### Squash (Combinar commits)

```bash
# Rebase interactivo de últimos 3 commits
git rebase -i HEAD~3

# En el editor, cambiar 'pick' a 'squash' para commits a combinar
# pick abc123 First commit
# squash def456 Second commit
# squash ghi789 Third commit

# Después, combina el mensaje si deseas
```

---

## Resolución de Conflictos

### Conflictos Típicos

```
<<<<<<< HEAD (Current branch)
Mi cambio aquí
=======
Su cambio aquí
>>>>>>> rama-que-mergean
```

### Resolver Manualmente

```bash
# 1. Ver conflictos
git status

# 2. Editar archivo y decidir qué cambio mantener
# 3. Eliminar marcadores de conflicto

# 4. Agregar archivo resuelto
git add archivo-resuelto.ts

# 5. Completar merge/rebase
git commit -m "merge: resolver conflictos"
# o
git rebase --continue  # si estás rebasando
```

### Resolver con Herramientas

```bash
# Usar merge tool gráfico
git mergetool

# Ver diferencias
git diff
git diff --ours
git diff --theirs

# Aceptar su versión completamente
git checkout --theirs file.ts
git add file.ts

# Aceptar nuestra versión completamente
git checkout --ours file.ts
git add file.ts
```

### Abortar Merge/Rebase

```bash
# Abortar merge
git merge --abort

# Abortar rebase
git rebase --abort

# Abortar cherry-pick
git cherry-pick --abort
```

---

## Comandos Útiles

### Inspeccionar Cambios

```bash
# Diferencias entre working dir y staging
git diff

# Diferencias entre staging y último commit
git diff --staged
# o
git diff --cached

# Diferencias entre ramas
git diff develop...feature/nueva

# Diferencias en archivo específico
git diff -- archivo.ts

# Mostrar archivo en commit específico
git show <hash>:archivo.ts

# Buscar cambios que contienen texto
git log -S "texto" --oneline
```

### Guardar Cambios Temporalmente

```bash
# Guardar cambios sin hacer commit
git stash

# Ver cambios guardados
git stash list

# Aplicar cambios guardados
git stash apply stash@{0}

# Aplicar y eliminar
git stash pop

# Descartar cambios guardados
git stash drop stash@{0}
```

### Limpiar Repositorio

```bash
# Eliminar archivos no trackeados
git clean -fd

# Dry run (ver qué se eliminaría)
git clean -fdn

# Descartar todos los cambios locales
git reset --hard
```

### Información del Repositorio

```bash
# Remotes configurados
git remote -v

# Información de rama
git branch -vv

# Mostrar cambios no pusheados
git log --branches --not --remotes

# Contar commits
git rev-list --all --count
```

---

## Troubleshooting

### "Detached HEAD"

```bash
# Problema: Estás en un commit, no en una rama

# Solución 1: Volver a rama
git checkout main

# Solución 2: Crear rama desde actual
git checkout -b nueva-rama
```

### "Permission Denied (publickey)"

```bash
# Problema: SSH key no configurada

# Soluciones:
# 1. Generar SSH key
ssh-keygen -t ed25519 -C "tu.email@example.com"

# 2. Agregar a SSH agent
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519

# 3. Agregar key a GitHub
# Copiar contenido de ~/.ssh/id_ed25519.pub
# Ir a GitHub Settings → SSH keys → Add key
```

### "Can't push, remote has changes"

```bash
# Solución: Primero pull
git pull origin develop
# Resolver conflictos si hay
git push origin develop
```

### Historial Arruinado

```bash
# Ver historial de referencias
git reflog

# Volver a punto anterior
git reset --hard <hash>

# CUIDADO: Esto es destructivo y no siempre recuperable
```

---

## Mejores Prácticas

### ✅ DO's

1. **Hacer commits pequeños** - Cada commit = un cambio lógico
2. **Escribir mensajes claros** - Futura ti te lo agradecerá
3. **Pullear antes de pushear** - Evita conflictos
4. **Crear ramas para features** - Mantén develop limpio
5. **Revisar cambios antes de commitear** - `git diff` antes de `git add`

### ❌ DON'Ts

1. **No hacer rebase en ramas compartidas** - 💥 Reescribe historial
2. **No commitear en main/develop directamente** - Usa PR
3. **No usar `git push -f`** - Es destructivo
4. **No commitear archivos grandes** - Usa .gitignore
5. **No ignorar conflictos de merge** - Resuelvelos adecuadamente

---

## Recursos Útiles

- [Pro Git Book](https://git-scm.com/book/en/v2)
- [Git Cheat Sheet](https://github.github.com/training-kit/github-git-cheat-sheet/)
- [Oh My Git!](https://ohmygit.org/) - Juego para aprender Git
- [Visualizing Git](https://git-school.github.io/visualizing-git/)

---

**Última actualización:** 22 de febrero de 2026  
**Versión:** 1.0.0
