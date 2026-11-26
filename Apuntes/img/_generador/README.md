# Generador de figuras

Las figuras de redes de Petri y de autómatas de `Apuntes/img` se generan con estos scripts.
`pnlib.py` dibuja plazas, transiciones, arcos (normales, inhibidores, lectores y reset), estados y
bucles, y al guardar avisa si algún texto se pisa con otro, con un arco o con un nodo, o si se sale
del dibujo.

Desde esta carpeta, para regenerar todo en `Apuntes/img`:

```powershell
python figs_pn.py ..
```

```powershell
python figs_pn2.py ..
```

```powershell
python figs_lr.py ..
```

```powershell
python figs_clases.py ..
```

`figs_clases.py` tiene los ejemplos de cada clase de red (pura, libre de conflicto, libre elección,
simple y sus extendidas) y las dos redes de los filósofos.

Para regenerar una sola figura se pasa su nombre después de la carpeta de salida, por ejemplo
`python figs_pn2.py .. pn-barberia`. `lr-pila.svg`, `lr-turing.svg` y `lr-jerarquia-chomsky.svg`
están hechas a mano y no se generan.
