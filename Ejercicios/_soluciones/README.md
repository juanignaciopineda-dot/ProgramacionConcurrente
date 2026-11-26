# Soluciones de referencia

> [!warning] Abrilas después de intentar el ejercicio
> Acá está resuelto todo lo que los esqueletos dejan con `TODO`. Si mirás la solución antes de
> trabarte de verdad, el verificador te va a dar verde pero no vas a haber practicado nada. Antes de
> venir acá, probá con las pistas plegadas del README del ejercicio.

Cada carpeta copia la estructura del ejercicio: los mismos archivos y el mismo `Verificador.java`,
pero con los métodos implementados. Todas pasan todos los casos del verificador.

## Cómo usarlas

- **Para comparar, no para copiar.** Cuando tu versión ya esté en verde, compará las dos: dónde está
  cada `synchronized`, qué objeto es la llave, quién hace `notifyAll()`, en qué orden se cierran y se
  esperan las cosas. Que haya diferencias no quiere decir que tu versión esté mal. Lo que importa es
  que puedas explicar por qué funciona.
- **Para salir de un `[COLGADO]`.** Si tu versión se cuelga y no encontrás por qué, mirá solo el
  método donde se queda esperando.
- **Para hacer los experimentos de "Para pensar"** sobre una versión que sabés que anda: cambiá un
  `notifyAll()` por `notify()`, un `while` por un `if`, o sacá un `join()`, y mirá qué caso falla.

Se corren igual que los ejercicios. Desde la carpeta de la solución:

```powershell
java src/Verificador.java
```

## Índice

| Unidad | Ejercicio | Archivos con la solución |
| ------ | --------- | ------------------------ |
| 1 | [1.1 Crear hilos](U1_Hilos/E1_1_crear_hilos/src) | `Tarea`, `HiloContador`, `Lanzador` |
| 1 | [1.2 Suma paralela](U1_Hilos/E1_2_suma_paralela/src) | `SumadorDeTramo`, `SumaParalela` |
| 1 | [1.3 Estados](U1_Hilos/E1_3_estados/src) | `FichaDeHilo`, `Recorrido` |
| 1 | [1.4 Interrupción](U1_Hilos/E1_4_interrupcion/src) | `Latido`, `BuscadorDePrimos`, `Interrupciones` |
| 1 | [1.5 Servicios del hilo](U1_Hilos/E1_5_servicios_del_hilo/src) | `RegistroDeFallas`, `Servicios`, `NumeradorPorHilo` |
| 2 | [2.1 Molinetes](U2_Exclusion_mutua/E2_1_molinetes/src) | `Molinete` |
| 2 | [2.2 Teatro](U2_Exclusion_mutua/E2_2_teatro/src) | `Teatro`, `ContadorDeFunciones` (con el bug corregido) |
| 2 | [2.3 Buffer acotado](U2_Exclusion_mutua/E2_3_buffer_acotado/src) | `BufferAcotado` |
| 2 | [2.4 Turnos](U2_Exclusion_mutua/E2_4_turnos/src) | `Turnero`, `Participante` |
| 2 | [2.5 Cola cerrable](U2_Exclusion_mutua/E2_5_cola_cerrable/src) | `ColaCerrable`, `Empaquetador`, `Despachante`, `Pipeline` |
| 3 | [3.1 Cuenta con `ReentrantLock`](U3_Locks_y_semaforos/E3_1_cuenta_con_lock/src) | `Cuenta` |
| 3 | [3.2 Catálogo de precios](U3_Locks_y_semaforos/E3_2_catalogo_lectores_escritores/src) | `Catalogo` |
| 3 | [3.3 Estacionamiento](U3_Locks_y_semaforos/E3_3_estacionamiento/src) | `Estacionamiento` |
| 3 | [3.4 Buffer con semáforos y largada](U3_Locks_y_semaforos/E3_4_buffer_con_semaforos/src) | `BufferConSemaforos`, `Largada` |
| 3 | [3.5 La cena de los filósofos](U3_Locks_y_semaforos/E3_5_filosofos/src) | `Mesa` |
| 4 | [4.1 Arranque de un servidor](U4_Herramientas_de_alto_nivel/E4_1_arranque_con_latch/src) | `Modulo`, `Tablero`, `Arranque` |
| 4 | [4.2 Fábrica de hilos](U4_Herramientas_de_alto_nivel/E4_2_fabrica_de_hilos/src) | `FabricaDeHilos` |
| 4 | [4.3 Servidor con pool fijo](U4_Herramientas_de_alto_nivel/E4_3_servidor_con_pool/src) | `Pedido`, `RegistroDeRechazos`, `Servidor` |
| 4 | [4.4 Callable y Future](U4_Herramientas_de_alto_nivel/E4_4_callable_y_future/src) | `TramoDePrimos`, `Paralelo` |
| 4 | [4.5 Carrera por etapas](U4_Herramientas_de_alto_nivel/E4_5_carrera_con_phaser/src) | `Carrera`, `Corredor` |
| 5 | [5.1 Buffer con dos condiciones](U5_Monitores/E5_1_buffer_con_condiciones/src) | `BufferConCondiciones` |
| 5 | [5.2 Puente de una mano](U5_Monitores/E5_2_puente_de_una_mano/src) | `Puente` |
| 5 | [5.3 Monitor con signal and exit](U5_Monitores/E5_3_monitor_signal_and_exit/src) | `ColaDeCondicion`, `SalaDeImpresion` |
| 5 | [5.4 Monitor de Hoare](U5_Monitores/E5_4_monitor_de_hoare/src) | `MonitorHoare`, `BufferHoare` |
| 5 | [5.5 Ventanilla con política](U5_Monitores/E5_5_ventanilla_con_politica/src) | `PoliticaPrioridad`, `PoliticaAlternada`, `Ventanilla` |
| 6 | [6.1 Cadenas y lenguajes](U6_Lenguajes_y_automatas/E6_1_cadenas_y_lenguajes/src) | `Lenguajes` |
| 6 | [6.2 Gramáticas y la jerarquía de Chomsky](U6_Lenguajes_y_automatas/E6_2_gramaticas_y_chomsky/src) | `Gramatica` |
| 6 | [6.3 Autómata finito determinista](U6_Lenguajes_y_automatas/E6_3_automata_finito/src) | `AFD`, `Automatas` |
| 6 | [6.4 Moore, Mealy y un sistema reactivo](U6_Lenguajes_y_automatas/E6_4_moore_y_mealy/src) | `MaquinaMealy`, `MaquinaMoore`, `Detectores`, `Conversion`, `DetectorReactivo` |
| 6 | [6.5 Autómata de pila](U6_Lenguajes_y_automatas/E6_5_automata_de_pila/src) | `AutomataDePila`, `AutomatasDePila` |
| 6 | [6.6 AFND, subconjuntos y gramáticas regulares](U6_Lenguajes_y_automatas/E6_6_afnd_y_subconjuntos/src) | `AFND`, `ALaMano` |
| 6 | [6.7 Expresiones regulares en Java](U6_Lenguajes_y_automatas/E6_7_expresiones_regulares_en_java/src) | `ExpresionesRegulares` |
| 7 | [7.1 La red y la ecuación de estado](U7_Redes_de_Petri/E7_1_red_y_ecuacion_de_estado/src) | `RedDePetri` |
| 7 | [7.2 Invariantes](U7_Redes_de_Petri/E7_2_invariantes/src) | `Invariantes`, `RedVigilada` |
| 7 | [7.3 Grafo de alcanzabilidad](U7_Redes_de_Petri/E7_3_grafo_de_alcanzabilidad/src) | `Explorador` |
| 7 | [7.4 Red con tiempo](U7_Redes_de_Petri/E7_4_red_temporizada/src) | `RedTemporizada` |
| 7 | [7.5 El monitor de la red](U7_Redes_de_Petri/E7_5_monitor_de_la_red/src) | `ProductorConsumidor`, `MonitorDeRed`, `Productor`, `Consumidor` |
| 7 | [7.6 T-invariantes en el log](U7_Redes_de_Petri/E7_6_t_invariantes_en_un_log/src) | `AnalizadorDeLog` |
