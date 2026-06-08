## Patrón de sincronización por barrera
## Autor: **Diego Andrés Ortiz Sanabria**  
**Escuela Colombiana de Ingeniería Julio Garavito**  
**Arquitecturas de Software (ARSW)**
 
---
 
## Descripción
 
Este laboratorio explora el **patrón de sincronización por barrera** en programación concurrente con Java. El programa base ejecuta N hilos que realizan una misma tarea a velocidades diferentes, y el objetivo es garantizar que el cálculo del tiempo promedio de ejecución se realice únicamente cuando el último hilo haya terminado.
 
---
 
## Estructura del proyecto
 
```
BarrierSyncProblem/
├── src/
│   └── edu/eci/arsw/samples/
│       ├── Main.java
│       └── HiloProc.java
├── bin/
├── .classpath
└── .project
```
 
--- 
 
## Ejecución del programa original
 
Al ejecutar el programa sin ninguna modificación, el resultado obtenido es:
 
<img width="1274" height="303" alt="image" src="https://github.com/user-attachments/assets/94bdc8ec-5596-4b5e-a848-5799021e9c08" />

 
### ¿Por qué el resultado es incorrecto?
 
El resultado es **0** porque el hilo principal (`main`) **no espera** a que los 20 hilos terminen su ejecución. En cuanto lanza los hilos con `.start()`, inmediatamente pasa a leer `getResultado()` de cada uno, pero en ese momento ningún hilo ha terminado y el campo `resultado` aún vale `0`. Por lo tanto, el promedio calculado es `0/20 = 0`.
 
 
---
 
## Solución con `CountDownLatch`
 
Para corregir el problema se aplicó el **patrón de sincronización por barrera** usando `CountDownLatch` de `java.util.concurrent`.
 
### ¿Cómo funciona `CountDownLatch`?
 
Un `CountDownLatch` actúa como un contador regresivo con una puerta bloqueada:
- Se inicializa con un valor N (en este caso, 20 hilos).
- Cualquier thread que llame `await()` queda bloqueado hasta que el contador llegue a 0.
- Cada hilo que termina su tarea llama `countDown()`, decrementando el contador en 1.
- Cuando el último hilo llama `countDown()` y el contador llega a 0, el `await()` se desbloquea y el main continúa.
### Flujo de ejecución corregido
 
```
Main                          Hilo 0     Hilo 1  ...  Hilo 19
 |                              |           |             |
 |-- start() todos -----------> |           |             |
 |                           corriendo   corriendo    corriendo
 |-- latch.await() --[BLOQUEADO]
 |                              |           |             |
 |                         countDown()      |             |
 |                           (19)           |             |
 |                                     countDown()        |
 |                                      (18)              |
 |                                                   countDown()
 |                                                     (0) ← último
 |<-----------------[DESBLOQUEADO]-----------------------
 |
 |-- calcula promedio (todos los resultados ya están listos)
```
 
### Cambios realizados en `HiloProc.java`
 
Se agregó un campo `CountDownLatch` y se modificó el constructor para recibirlo. Al finalizar el método `run()`, el hilo llama `latch.countDown()` para notificar que terminó.
 
```java
 
public class HiloProc extends Thread {

    CountDownLatch latch; // ← NUEVO
 
    public HiloProc(int id, CountDownLatch latch) {
        ...
        this.latch = latch; // ← NUEVO
    }
 
    public void run() {
        ...
        latch.countDown(); // ← NUEVO: avisa que este hilo terminó
    }
 
}
```
 
### Cambios realizados en `Main.java`
 
Se creó el `CountDownLatch` con valor 20, se pasó a cada hilo, y se añadió `latch.await()` antes del cálculo del promedio.
 
```java;
 
public class Main {
 
    public static void main(String[] args) throws InterruptedException {
        int numHilos = 20;
 
        CountDownLatch latch = new CountDownLatch(numHilos); // ← NUEVO

        ...
 
        latch.await(); // ← NUEVO: main se bloquea hasta que todos terminen
 
        long tiempoPromedio = 0;
        for (int i = 0; i < numHilos; i++) {
            tiempoPromedio += hilos[i].getResultado();
        }
 
        System.out.println("El tiempo promedio de la ejecución fue de:" + tiempoPromedio / numHilos);
    }
}
```
 
---
 
## Verificación
 
### Resultado antes de la corrección
 
```
El tiempo promedio de la ejecución fue de: 0   ← incorrecto, aparece al inicio
Soy el hilo 5 y voy en el 10.0% de mi tarea. P:2947
...
```
 
### Resultado después de la corrección
 
<img width="1273" height="274" alt="image" src="https://github.com/user-attachments/assets/415c7e5c-4f47-4426-a374-934be3269694" />

El mensaje del promedio ahora aparece **al final**, una vez que todos los hilos han completado su tarea al 100%.
 
---

 
## Compilación y ejecución
 
```powershell
javac -d bin src/edu/eci/arsw/samples/*.java
java -cp bin edu.eci.arsw.samples.Main
```
 
