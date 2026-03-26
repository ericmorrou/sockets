# Práctica de Sockets - Guía Rápida

Este proyecto contiene varias implementaciones de sockets en Java para la Actividad 1.

## 🚀 Cómo empezar

Primero, compila todos los archivos:
```powershell
javac Part1/*.java Part2/*.java Part4/*.java
```

### 1. Chat entre dos clientes (Part1)
Esta versión permite que dos clientes hablen entre sí a través del servidor.

1. Arranca el servidor:
   ```powershell
   java Part1.ChatServer
   ```
2. Arranca el **primer** cliente en otra terminal:
   ```powershell
   java Part1.ChatClient
   ```
3. Arranca el **segundo** cliente en una tercera terminal:
   ```powershell
   java Part1.ChatClient
   ```
4. Escribe mensajes en cualquiera de las terminales de cliente. Para salir, escribe `EXIT`.

### 2. Servidor de Tickets (Part2)
Elige una versión del servidor (V1, V2, V3, Synchronized o Atomic):
```powershell
java Part2.TicketServerAtomic
```
Para probar si hay duplicados, usa el cliente de estrés (puedes pasarle el puerto como argumento):
```powershell
java Part2.TicketClient 6004
```

### 3. Simulación Apache (Part4)
Entra en la carpeta `Part4` y ejecuta el servidor:
```powershell
cd Part4
java MiniApache
```
Luego abre `http://localhost:8080` en tu navegador.

---
Para más detalles sobre la implementación y la teoría de red, consulta el archivo `walkthrough.md`.
