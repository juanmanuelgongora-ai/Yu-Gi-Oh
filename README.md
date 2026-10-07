# Yu-Gi-Oh! Duel Lite 🎴⚔️

Mini-aplicación de escritorio desarrollada en **Java Swing** que simula un duelo sencillo de cartas entre un Jugador y la Máquina, consumiendo en tiempo real la API pública de **YGOProDeck**.

---

## 📌 Información del Laboratorio
* **Programa:** Tecnología en Sistemas.
* **Materia:** Desarrollo de Software III.
* **Docente:** Mg(c). Juan Pablo Pinillos Reina.

---

## 🚀 Requisitos y Tecnologías
* **Lenguaje:** Java 11 o superior.
* **Interfaz Gráfica:** Swing.
* **Cliente HTTP y Parsing JSON:** `java.net.http.HttpClient` + `org.json`.
* **Gestor de Proyecto:** Maven.

---

## 🎯 Características e Integración
1. **Consumo de API REST:** Consulta `randomcard.php` garantizando que solo se carguen 3 cartas de tipo **Monster** por participante.
2. **Desacoplamiento Lógica/UI:** La clase `Duel` no conoce a Swing. Se comunica mediante la interfaz `BattleListener` (`onTurn`, `onScoreChanged`, `onDuelEnded`).
3. **Manejo de Hilos (No Bloqueante):** Uso de `SwingWorker` en la carga de cartas e imágenes web para no congelar el hilo principal de la UI (`EDT`).
4. **Sistema de Batalla:** Comparación de estadísticas ATK/DEF y victoria al primero en ganar **2 de 3 rondas**. Log de batalla desplazable (`JTextArea` + `JScrollPane`).

---

## 🛠️ Instrucciones de Ejecución

### Opción 1: Desde la Consola / Terminal
```bash
# Compilar el proyecto con Maven
mvn clean compile

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.yugioh.duellite.Main"
```

### Opción 2: Desde un IDE
1. Abrir la carpeta en IntelliJ IDEA, Eclipse o VS Code.
2. Ejecutar la clase principal: `com.yugioh.duellite.Main` (o `com.yugioh.duellite.ui.MainFrame`).

---

## 🏗️ Breve Explicación de Diseño (Arquitectura POO)

El proyecto implementa una arquitectura limpia y separada en paquetes:

* **`com.yugioh.duellite.model.Card`**: Encapsula las propiedades de la carta (nombre, tipo, ATK, DEF e URL de la imagen).
* **`com.yugioh.duellite.api.YgoApiClient`**: Cliente HTTP de Java 11+ encargado del consumo REST, soporte de redirecciones 301, parsing de `data[0]` y filtrado de cartas tipo Monstruo.
* **`com.yugioh.duellite.logic.Duel`**: Reglas del negocio, gestión de mazos y conteo de victorias (2 de 3).
* **`com.yugioh.duellite.logic.BattleListener`**: Interfaz de eventos que notifica a la interfaz gráfica.
* **`com.yugioh.duellite.ui.CardPanel` & `MainFrame`**: Componentes visuales Swing para mostrar las cartas, fotos, marcador y log de eventos.
