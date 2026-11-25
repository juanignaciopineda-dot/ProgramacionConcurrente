// Genera los cuestionarios de multiple choice (uno por parcial y uno para el final)
// a partir de los cuestionarios completos y de plantilla-multiple-choice.html.
//
// Uso (desde esta carpeta):   node generar-multiple-choice.js
//
// Corrélo cada vez que agregues o corrijas preguntas en los cuestionarios completos.
// Antes de generar, sincroniza el final con los parciales (Final/sincronizar-final.js),
// así que con este comando alcanza para dejar todo al día.
const fs = require('fs');
const path = require('path');

require('./Final/sincronizar-final.js');

const aqui = __dirname;
const plantilla = fs.readFileSync(path.join(aqui, 'plantilla-multiple-choice.html'), 'utf8');
const deExamen = p => !/^(apunte|filmina|integradora)/i.test(p.src);

function cargar(rel) {
  const html = fs.readFileSync(path.join(aqui, rel), 'utf8');
  const script = html.match(/<script>([\s\S]*?)<\/script>/)[1];
  const datos = script.slice(0, script.indexOf('/* ---------- Utilidades'));
  return new Function(datos + '\nreturn { T, BANCO };')();
}

const SALIDAS = [
  { fuente: 'Primer Parcial/Cuestionario-Parcial-1.html', destino: 'Primer Parcial/Multiple-Choice-Parcial-1.html',
    titulo: 'Multiple choice del primer parcial', key: 'pc-mc-parcial1-hist', examen: 20 },
  { fuente: 'Segundo parcial/Cuestionario-Parcial-2.html', destino: 'Segundo parcial/Multiple-Choice-Parcial-2.html',
    titulo: 'Multiple choice del segundo parcial', key: 'pc-mc-parcial2-hist', examen: 20 },
  { fuente: 'Final/Cuestionario-Final.html', destino: 'Final/Multiple-Choice-Final.html',
    titulo: 'Multiple choice del final', key: 'pc-mc-final-hist', examen: 30,
    bloques: { '1er parcial': 10, '2do parcial': 12, 'Integradora': 8 } }
];

for (const s of SALIDAS) {
  const { T, BANCO } = cargar(s.fuente);
  const sinMC = BANCO.filter(p => !p.mc);
  if (sinMC.length) console.warn(`  ${s.fuente}: ${sinMC.length} preguntas sin opción múltiple quedan afuera (${sinMC.map(p => p.id).join(', ')})`);
  const items = BANCO.filter(p => p.mc).map(p => {
    const it = { id: p.id, tema: p.tema, src: p.src, ex: deExamen(p), q: p.mc.q || p.q, o: p.mc.o, c: p.mc.c, r: p.r };
    if (p.b) it.b = p.b;
    if (p.n) it.n = p.n;
    return it;
  });

  const nEx = items.filter(p => p.ex).length;
  let bajada;
  if (s.bloques) {
    const cuenta = b => items.filter(p => p.b === b).length;
    const reparto = Object.entries(s.bloques).map(([b, n]) => `${n} ${b === 'Integradora' ? 'integradoras' : 'del ' + b.replace('1er', 'primer').replace('2do', 'segundo')}`);
    bajada = `${items.length} preguntas de opción múltiple sobre toda la materia, ${cuenta('1er parcial')} del primer parcial, ${cuenta('2do parcial')} del segundo y ${cuenta('Integradora')} integradoras que cruzan temas. ` +
      `El examen saca ${s.examen} al azar (${reparto.slice(0, -1).join(', ')} y ${reparto[reparto.length - 1]}) y se corrige al entregar.`;
  } else {
    bajada = `${items.length} preguntas de opción múltiple. ${nEx} salieron en parciales viejos y ${items.length - nEx} están armadas con el apunte. ` +
      `El examen saca ${s.examen} al azar y se corrige al entregar.`;
  }
  bajada += ' En la práctica, cada respuesta se corrige en el momento. Cada opción trae la explicación de por qué es correcta o no, y abajo está la respuesta desarrollada, la misma del cuestionario escrito.';

  const conf = { TITULO: s.titulo, BAJADA: bajada, KEY: s.key, EXAMEN: s.examen, BLOQUES: s.bloques || null };
  const datos = (`const CONF = ${JSON.stringify(conf)};\n` +
    `const T = ${JSON.stringify(T, null, 1)};\n` +
    `const BANCO = [\n${items.map(it => JSON.stringify(it)).join(',\n')}\n];`).replace(/<\//g, '<\\/');

  // Verifica que el bloque de datos se pueda ejecutar antes de escribir.
  const prueba = new Function(datos + '\nreturn BANCO.length;')();
  const html = plantilla.replace('/*__DATOS__*/', () => datos).replace('__TITULO__', s.titulo);
  fs.writeFileSync(path.join(aqui, s.destino), html);
  console.log(`${s.destino}: ${prueba} preguntas`);
}
